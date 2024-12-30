package my.benzol45.bookservice.Integration

import my.benzol45.bookservice.model.response.MemberDto
import my.benzol45.bookservice.repository.MemberRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class MemberIntegrationTest(
    @Autowired private val memberRepository: MemberRepository
) {
    companion object {
        val postgresTestContainer = PostgreSQLContainer("postgres:15")
            .apply {
                withDatabaseName("testdb")
                withUsername("testuser")
                withPassword("testpass")
            }

        @BeforeAll
        @JvmStatic
        fun startDBContainer() {
            postgresTestContainer.start()
        }

        @AfterAll
        @JvmStatic
        fun stopDBContainer() {
            postgresTestContainer.stop()
        }

        @DynamicPropertySource
        @JvmStatic
        fun registerDBContainer(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresTestContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresTestContainer::getUsername)
            registry.add("spring.datasource.password", postgresTestContainer::getPassword)
        }
    }

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    @Order(1)
    fun `register new member`() {
        // Arrange
        val bookJson = """
            {
              "name": "John",
              "surname": "Doe",
              "email": "john.doe@example.com",
              "phone": "1234567890"
            }                        
        """.trimIndent()
        val request = HttpEntity<String>(bookJson, HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        })

        // Act
        val response = restTemplate.postForEntity("/members", request, MemberDto::class.java)

        // Assert
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        val newMemberId = response.body!!.id
        with(memberRepository.findById(newMemberId).orElseThrow()) {
            assertEquals("John", name)
            assertEquals("Doe", surname)
            assertEquals("john.doe@example.com", email)
            assertEquals("1234567890", phone)
        }
    }

    @Test
    @Order(2)
    fun `get all members`() {
        // Arrange

        // Act
        val response = restTemplate.getForEntity("/members", Map::class.java)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(1, (response.body!!["content"]!! as List<*>).size)
    }

    @Test
    @Order(3)
    fun `get member by id`() {
        // Arrange

        // Act
        val response = restTemplate.getForEntity("/members/1", MemberDto::class.java)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        with(response.body!!) {
            assertEquals("John", name)
            assertEquals("Doe", surname)
            assertEquals("john.doe@example.com", email)
            assertEquals("1234567890", phone)
        }
    }

    @Test
    @Order(4)
    fun `filter member`() {
        // Arrange

        // Act
        val responseCorrectFilter = restTemplate.getForEntity("/members/filter?surname=doe&email=john", List::class.java)
        val responseEmptyResult = restTemplate.getForEntity("/members/filter?name=gordon&surname=freeman&email=john", List::class.java)

        // Assert
        assertEquals(HttpStatus.OK, responseCorrectFilter.statusCode)
        assertNotNull(responseCorrectFilter.body)
        assertEquals(1, responseCorrectFilter.body!!.size)

        assertEquals(HttpStatus.OK, responseEmptyResult.statusCode)
        assertNotNull(responseEmptyResult.body)
        assertEquals(0, responseEmptyResult.body!!.size)
    }

    @Test
    @Order(5)
    fun `block member`() {
        // Arrange

        // Act
        restTemplate.patchForObject("/members/1/block", null, String::class.java)


        // Assert
        assertTrue { memberRepository.findById(1).orElseThrow().block }
    }

    @Test
    @Order(5)
    fun `unblock member`() {
        // Arrange

        // Act
        restTemplate.patchForObject("/members/1/unblock", null, String::class.java)


        // Assert
        assertFalse { memberRepository.findById(1).orElseThrow().block }
    }
}