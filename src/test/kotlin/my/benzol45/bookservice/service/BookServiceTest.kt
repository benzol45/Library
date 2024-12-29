package my.benzol45.bookservice.service

import my.benzol45.bookservice.domain.AvailableBook
import my.benzol45.bookservice.domain.Book
import my.benzol45.bookservice.mapper.BookMapper
import my.benzol45.bookservice.model.request.BookImportDto
import my.benzol45.bookservice.model.request.NewBookDto
import my.benzol45.bookservice.model.response.BookDto
import my.benzol45.bookservice.repository.AvailableBookRepository
import my.benzol45.bookservice.repository.BookRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

@ExtendWith(MockitoExtension::class)
class BookServiceTest {

    @Mock
    private lateinit var bookRepository: BookRepository
    @Mock
    private lateinit var availableBookRepository: AvailableBookRepository
    @Mock
    private lateinit var bookMapper: BookMapper
    @Mock
    private lateinit var bookImportService: BookImportService
    @InjectMocks
    private lateinit var bookService: BookService

    private val TEST_ID = 1L
    private val TEST_TITLE = "Test title"
    private val TEST_AUTHOR = "Test Author"
    private val TEST_ISBN = "123456789012"

    @Test
    fun `getAllBooks should return a page of books`() {
        // Arrange
        val pageable: Pageable = PageRequest.of(0, 10)
        val book = getTestBook()
        val bookDto = getTestBookDto()
        val bookPage = PageImpl(listOf(book), pageable, 1)

        Mockito.`when`(bookRepository.findAll(pageable)).thenReturn(bookPage)
        Mockito.`when`(bookMapper.bookToBookDTO(book)).thenReturn(bookDto)

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

        Mockito.`when`(bookRepository.findAllByTitleAndAuthor(TEST_TITLE, TEST_AUTHOR)).thenReturn(listOf(book))
        Mockito.`when`(bookMapper.bookToBookDTO(book)).thenReturn(bookDto)

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

        Mockito.`when`(bookRepository.findById(TEST_ID)).thenReturn(java.util.Optional.of(book))
        Mockito.`when`(bookMapper.bookToBookDTO(book)).thenReturn(bookDto)

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
        val newBookDto = getTestNewBookDto()
        val bookDto = getTestBookDto()

        Mockito.`when`(bookRepository.getFirstByIsbn(TEST_ISBN)).thenReturn(null)
        Mockito.`when`(bookImportService.importBook(bookImportDto)).thenReturn(newBookDto)
        Mockito.`when`(bookMapper.newBookDTOToBook(newBookDto)).thenReturn(book)
        Mockito.`when`(bookRepository.save(book)).thenReturn(book)
        Mockito.`when`(bookMapper.bookToBookDTO(book)).thenReturn(bookDto)

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

        Mockito.`when`(bookRepository.getFirstByIsbn(TEST_ISBN)).thenReturn(null)
        Mockito.`when`(bookMapper.newBookDTOToBook(newBookDto)).thenReturn(book)
        Mockito.`when`(bookRepository.save(book)).thenReturn(book)
        Mockito.`when`(bookMapper.bookToBookDTO(book)).thenReturn(bookDto)
        Mockito.`when`(availableBookRepository.findFirstByBook(book)).thenReturn(availableBook)
        Mockito.`when`(availableBookRepository.save(Mockito.any(AvailableBook::class.java))).thenReturn(availableBook)

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
