package my.benzol45.bookservice.Integration

import my.benzol45.bookservice.model.response.BookCheckedOutDto
import my.benzol45.bookservice.model.response.BookReturnResultDto
import my.benzol45.bookservice.repository.AvailableBookRepository
import my.benzol45.bookservice.repository.BookRepository
import my.benzol45.bookservice.repository.CheckedOutBookRepository
import my.benzol45.bookservice.repository.MemberRepository
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.*
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.jdbc.Sql
import org.testcontainers.containers.PostgreSQLContainer
import java.time.LocalDate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = ["/TestData.sql"])
class BookCheckoutIntegrationTest(
    @Autowired private val checkedOutBookRepository: CheckedOutBookRepository,
    @Autowired private val availableBookRepository: AvailableBookRepository,
    @Autowired private val bookRepository: BookRepository,
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
    fun`full checkout flow`() {
        checkoutUnavailableBook()
        checkoutAvailableBook()
        getMemberBooks()
        returnBook()
    }

    fun checkoutUnavailableBook() {
        // Arrange
        val memberJson = """
            {
            "memberId": 1,
            "returnDate": "3024-12-30"            
            }            
        """.trimIndent()
        val request = HttpEntity<String>(memberJson, HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        })

        // Act
        val response = restTemplate.exchange<BookCheckedOutDto>("/books/2/checkout", HttpMethod.PATCH, request)

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }

    fun checkoutAvailableBook() {
        // Arrange
        val memberJson = """
            {
            "memberId": 1,
            "returnDate": "3024-12-30"
            }
        """.trimIndent()
        val request = HttpEntity<String>(memberJson, HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        })

        // Act
        val response = restTemplate.exchange<BookCheckedOutDto>("/books/1/checkout", HttpMethod.PATCH, request)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        with(response.body!!) {
            assertEquals(1, bookId)
            assertEquals(1, memberId)
            assertEquals(LocalDate.now(), issueDate)
            assertEquals(LocalDate.of(3024,12,30), returnDate)
        }

        val book = bookRepository.findById(1).orElseThrow()
        val availableBook = availableBookRepository.findFirstByBook(book)!!
        with(availableBook) {
            assertEquals(2, amount)
            assertEquals(1, checkedOut)
            assertEquals(1, available)
        }

        val member = memberRepository.findById(1).orElseThrow()
        val checkedOutBook = checkedOutBookRepository.findFirstByMemberAndBook(member, book)!!
        with(checkedOutBook) {
            assertEquals(book, this.book)
            assertEquals(member.id, this.member.id)
            assertEquals(LocalDate.now(), issueDate)
            assertEquals(LocalDate.of(3024,12,30), returnDate)
        }
    }

    fun getMemberBooks() {
        // Arrange

        // Act
        val response = restTemplate.getForEntity("/members/1/books", List::class.java)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        with(response.body!!) {
            assertEquals(1, size)
            assertEquals(1, (get(0) as? Map<*, *>)?.get("id"))
        }
    }

    fun returnBook() {
        // Arrange
        val memberJson = """
            {
            "memberId": 1            
            }
        """.trimIndent()
        val request = HttpEntity<String>(memberJson, HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        })

        // Act
        val response = restTemplate.exchange<BookReturnResultDto>("/books/1/return", HttpMethod.PATCH, request)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        with(response.body!!) {
            assertEquals(true, onTime)
            assertEquals(LocalDate.now(), issueDate)
            assertEquals(LocalDate.of(3024,12,30), returnDate)
        }

        val book = bookRepository.findById(1).orElseThrow()
        val availableBook = availableBookRepository.findFirstByBook(book)!!
        with(availableBook) {
            assertEquals(2, amount)
            assertEquals(0, checkedOut)
            assertEquals(2, available)
        }

        val member = memberRepository.findById(1).orElseThrow()
        assertNull(checkedOutBookRepository.findFirstByMemberAndBook(member, book))
    }
}