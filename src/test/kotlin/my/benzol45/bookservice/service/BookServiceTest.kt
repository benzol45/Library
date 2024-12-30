package my.benzol45.bookservice.service

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import my.benzol45.bookservice.domain.AvailableBook
import my.benzol45.bookservice.domain.Book
import my.benzol45.bookservice.mapper.BookMapper
import my.benzol45.bookservice.model.request.BookImportDto
import my.benzol45.bookservice.model.request.NewBookDto
import my.benzol45.bookservice.model.response.BookDto
import my.benzol45.bookservice.repository.AvailableBookRepository
import my.benzol45.bookservice.repository.BookRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.util.*

@ExtendWith(MockKExtension::class)
class BookServiceTest {

    @MockK
    private lateinit var bookRepository: BookRepository
    @MockK
    private lateinit var availableBookRepository: AvailableBookRepository
    @MockK
    private lateinit var bookMapper: BookMapper
    @MockK
    private lateinit var bookImportService: BookImportService

    private lateinit var bookService: BookService

    private val TEST_ID = 1L
    private val TEST_TITLE = "Test title"
    private val TEST_AUTHOR = "Test Author"
    private val TEST_ISBN = "123456789012"

    @BeforeEach
    fun setUp() {
        bookService = BookService(bookRepository, availableBookRepository, bookMapper, bookImportService)
    }

    @Test
    fun `getAllBooks should return a page of books`() {
        // Arrange
        val pageable: Pageable = PageRequest.of(0, 10)
        val book = getTestBook()
        val bookDto = getTestBookDto()
        val bookPage = PageImpl(listOf(book), pageable, 1)

        every { bookRepository.findAll(pageable) } returns bookPage
        every { bookMapper.bookToBookDTO(book) } returns bookDto

        // Act
        val result = bookService.getAllBooks(pageable)

        // Assert
        assert(result.content.size == 1)
        assert(result.content[0].title == TEST_TITLE)
        assert(result.content[0].author == TEST_AUTHOR)
    }

    @Test
    fun `getFilteredBooks should return list of books`() {
        // Arrange
        val book = getTestBook()
        val bookDto = getTestBookDto()

        every { bookRepository.findAllByTitleAndAuthor(TEST_TITLE, TEST_AUTHOR) } returns listOf(book)
        every { bookMapper.bookToBookDTO(book) } returns bookDto

        // Act
        val result = bookService.getFilteredBooks(TEST_TITLE, TEST_AUTHOR)

        // Assert
        assert(result.size == 1)
        assert(result[0].title == TEST_TITLE)
        assert(result[0].author == TEST_AUTHOR)
    }

    @Test
    fun `getBook should return book dto`() {
        // Arrange
        val book = getTestBook()
        val bookDto = getTestBookDto()

        every { bookRepository.findById(TEST_ID) } returns Optional.of(book)
        every { bookMapper.bookToBookDTO(book) } returns bookDto

        // Act
        val result = bookService.getBook(TEST_ID)

        // Assert
        assert(result != null)
        assert(result?.title == TEST_TITLE)
        assert(result?.author == TEST_AUTHOR)
    }

    @Test
    fun `importBook should return imported book`() {
        // Arrange
        val bookImportDto = getTestBookImportDto()
        val book = getTestBook()
        val availableBook = getTestAvailableBook()
        val newBookDto = getTestNewBookDto()
        val bookDto = getTestBookDto()

        every { bookRepository.getFirstByIsbn(TEST_ISBN) } returns null
        every { bookImportService.importBook(bookImportDto) } returns newBookDto
        every { bookMapper.newBookDTOToBook(newBookDto) } returns book
        every { bookRepository.save(book) } returns book
        every { availableBookRepository.findFirstByBook(book) } returns availableBook
        every { availableBookRepository.save(any<AvailableBook>()) } returns availableBook

        every { bookMapper.bookToBookDTO(book) } returns bookDto
        // Act
        val result = bookService.importBook(bookImportDto)

        // Assert
        assert(result != null)
        assert(result?.title == TEST_TITLE)
        assert(result?.author == TEST_AUTHOR)
    }

    @Test
    fun `createBook should return created book dto`() {
        // Arrange
        val newBookDto = getTestNewBookDto()
        val book = getTestBook()
        val availableBook = getTestAvailableBook()
        val bookDto = getTestBookDto()

        every { bookRepository.getFirstByIsbn(TEST_ISBN) } returns null
        every { bookMapper.newBookDTOToBook(newBookDto) } returns book
        every { bookRepository.save(book) } returns book
        every { bookMapper.bookToBookDTO(book) } returns bookDto
        every { availableBookRepository.findFirstByBook(book) } returns availableBook
        every { availableBookRepository.save(any<AvailableBook>()) } returns availableBook

        // Act
        val result = bookService.createBook(newBookDto)

        // Assert
        assert(result != null)
        assert(result?.title == TEST_TITLE)
        assert(result?.author == TEST_AUTHOR)
    }

    private fun getTestBookDto() =
        BookDto(
            id = TEST_ID,
            title = TEST_TITLE,
            author = TEST_AUTHOR,
            isbn = TEST_ISBN,
            pages = 100,
            publisher = null,
            dateOfPublication = null)

    private fun getTestBook() =
        Book(
            id = TEST_ID,
            title = TEST_TITLE,
            author = TEST_AUTHOR,
            isbn = TEST_ISBN)

    private fun getTestBookImportDto() =
        BookImportDto(
            isbn = TEST_ISBN,
            amount = 10)

    private fun getTestNewBookDto() =
        NewBookDto(
            title = TEST_TITLE,
            author = TEST_AUTHOR,
            isbn = TEST_ISBN,
            amount = 10)

    private fun getTestAvailableBook() =
        AvailableBook(
            book = getTestBook(),
            amount = 10)
}
