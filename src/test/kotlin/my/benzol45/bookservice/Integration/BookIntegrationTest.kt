package my.benzol45.bookservice.Integration

import my.benzol45.bookservice.model.response.BookDto
import my.benzol45.bookservice.repository.AvailableBookRepository
import my.benzol45.bookservice.repository.BookRepository
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
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class BookIntegrationTest(
    @Autowired private val bookRepository: BookRepository,
    @Autowired private val availableBookRepository: AvailableBookRepository
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
    fun `create new book`() {
        // Arrange
        val bookJson = """
            {
              "isbn": "9780307743657",
              "author": "Stephen King",
              "title": "The Shining",
              "pages": 699,
              "publisher": "Random House Publishing",
              "dateOfPublication": "2012-01-01",
              "amount": 2
            }            
        """.trimIndent()
        val request = HttpEntity<String>(bookJson, HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        })

        // Act
        val response = restTemplate.postForEntity("/books", request, BookDto::class.java)

        // Assert
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        val newBookId = response.body!!.id
        val book = bookRepository.findById(newBookId).orElseThrow()
        with(book) {
            assertEquals("Stephen King", author)
            assertEquals("The Shining", title)
            assertEquals("9780307743657", isbn)
        }
        assertEquals(2, availableBookRepository.findFirstByBook(book)?.amount)
        assertEquals(0, availableBookRepository.findFirstByBook(book)?.checkedOut)
        assertEquals(2, availableBookRepository.findFirstByBook(book)?.available)
    }

    @Test
    @Order(2)
    fun `get all books`() {
        // Arrange

        // Act
        val response = restTemplate.getForEntity("/books", Map::class.java)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(1, (response.body!!["content"]!! as List<*>).size)
    }

    @Test
    @Order(3)
    fun `get book by id`() {
        // Arrange

        // Act
        val response = restTemplate.getForEntity("/books/1", BookDto::class.java)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        with(response.body!!) {
            assertEquals("Stephen King", author)
            assertEquals("The Shining", title)
            assertEquals("9780307743657", isbn)
        }
    }

    @Test
    @Order(4)
    fun `filter books`() {
        // Arrange

        // Act
        val responseCorrectFilter = restTemplate.getForEntity("/books/filter?title=Shining&author=King", List::class.java)
        val responseEmptyResult = restTemplate.getForEntity("/books/filter?title=Shining&author=Pushkin", List::class.java)

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
    fun `delete book`() {
        // Arrange

        // Act
        restTemplate.delete("/books/1")


        // Assert
        assertTrue { bookRepository.findById(1).isEmpty }
        assertEquals(0, bookRepository.findAll().size)
    }
}