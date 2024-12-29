package my.benzol45.bookservice.service

import my.benzol45.bookservice.domain.AvailableBook
import my.benzol45.bookservice.domain.Book
import my.benzol45.bookservice.domain.CheckedOutBook
import my.benzol45.bookservice.domain.Member
import my.benzol45.bookservice.mapper.CheckedOutBookMapper
import my.benzol45.bookservice.model.request.CheckoutDto
import my.benzol45.bookservice.model.request.MemberReferenceDto
import my.benzol45.bookservice.model.response.BookCheckedOutDto
import my.benzol45.bookservice.model.response.BookReturnResultDto
import my.benzol45.bookservice.repository.AvailableBookRepository
import my.benzol45.bookservice.repository.BookRepository
import my.benzol45.bookservice.repository.CheckedOutBookRepository
import my.benzol45.bookservice.repository.MemberRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.util.*

@ExtendWith(MockitoExtension::class)
class CheckoutBookServiceTest {
    @Mock
    private lateinit var checkedOutBookRepository: CheckedOutBookRepository
    @Mock
    private lateinit var bookRepository: BookRepository
    @Mock
    private lateinit var availableBookRepository: AvailableBookRepository
    @Mock
    private lateinit var memberRepository: MemberRepository
    @Mock
    private lateinit var checkedOutBookMapper: CheckedOutBookMapper
    @InjectMocks
    private lateinit var checkoutBookService: CheckoutBookService

    private val TEST_ID = 1L
    private val TEST_SURNAME = "Doe"
    private val TEST_EMAIL = "john.doe@example.com"
    private val TEST_PHONE = "1234567890"
    private val TEST_TITLE = "Test title"
    private val TEST_AUTHOR = "Test Author"
    private val TEST_ISBN = "123456789012"

    @Test
    fun `checkoutBook should successfully checkout a book`() {
        // Arrange
        val book =getTestBook()
        val availableBook = getTestAvailableBook()
        val member = getTestMember()
        val checkoutDto = getTestCheckoutDto()
        val checkedOutBook = getTestCheckedOutBook()
        val bookCheckedOutDto = getTestBookCheckedOutDto()

        Mockito.`when`(bookRepository.findById(TEST_ID)).thenReturn(Optional.of(book))
        Mockito.`when`(availableBookRepository.findFirstByBook(book)).thenReturn(availableBook)
        Mockito.`when`(memberRepository.findById(TEST_ID)).thenReturn(Optional.of(member))
        Mockito.`when`(checkedOutBookRepository.save(any())).thenReturn(checkedOutBook)
        Mockito.`when`(checkedOutBookMapper.checkedOutBookToCheckedOutBookDTO(checkedOutBook)).thenReturn(bookCheckedOutDto)

        // Act
        val result = checkoutBookService.checkoutBook(TEST_ID, checkoutDto)

        // Assert
        assert(result != null)
        assert(result?.bookId == TEST_ID)
        assert(result?.memberId == TEST_ID)
    }

    @Test
    fun `checkoutBook should return null when book is not available`() {
        // Arrange
        val book = getTestBook()
        val availableBook = getTestUnavailableBook()
        val checkoutDto = getTestCheckoutDto()

        Mockito.`when`(bookRepository.findById(TEST_ID)).thenReturn(Optional.of(book))
        Mockito.`when`(availableBookRepository.findFirstByBook(book)).thenReturn(availableBook)

        // Act
        val result = checkoutBookService.checkoutBook(TEST_ID, checkoutDto)

        // Assert
        assert(result == null)
    }

    @Test
    fun `returnBook should successfully return an on-time result`() {
        // Arrange
        val book = getTestBook()
        val availableBook = getTestAvailableBook()
        val member = getTestMember()
        val checkedOutBook = getTestCheckedOutBook()
        val memberReferenceDto = getTestMemberReferenceDto()
        val bookReturnResultDto = getTestBookReturnResultDto()

        Mockito.`when`(bookRepository.findById(TEST_ID)).thenReturn(Optional.of(book))
        Mockito.`when`(availableBookRepository.findFirstByBook(book)).thenReturn(availableBook)
        Mockito.`when`(memberRepository.findById(TEST_ID)).thenReturn(Optional.of(member))
        Mockito.`when`(checkedOutBookRepository.findFirstByMemberAndBook(member, book)).thenReturn(checkedOutBook)
        Mockito.`when`(checkedOutBookMapper.checkedOutBookToBookReturnResultDto(checkedOutBook)).thenReturn(bookReturnResultDto)

        // Act
        val result = checkoutBookService.returnBook(TEST_ID, memberReferenceDto)

        // Assert
        assert(result != null)
        assert(result?.onTime == true)
    }

    @Test
    fun `returnBook should return null when no checked out book found`() {
        // Arrange
        val book = getTestBook()
        val availableBook = getTestUnavailableBook()
        val memberReferenceDto = getTestMemberReferenceDto()

        Mockito.`when`(bookRepository.findById(TEST_ID)).thenReturn(Optional.of(book))
        Mockito.`when`(availableBookRepository.findFirstByBook(book)).thenReturn(availableBook)

        // Act
        val result = checkoutBookService.returnBook(TEST_ID, memberReferenceDto)

        // Assert
        assert(result == null)
    }

    private fun getTestMember() =
        Member(
            id = TEST_ID,
            surname = TEST_SURNAME,
            email = TEST_EMAIL,
            phone = TEST_PHONE,
            block = false
        )

    private fun getTestBook() =
        Book(
            id = TEST_ID,
            title = TEST_TITLE,
            author = TEST_AUTHOR,
            isbn = TEST_ISBN)

    private fun getTestAvailableBook() =
        AvailableBook(
            book = getTestBook(),
            amount = 15,
            available = 10,
            checkedOut = 5)

    private fun getTestUnavailableBook() =
        AvailableBook(
            book = getTestBook(),
            amount = 10,
            available = 0)

    private fun getTestCheckedOutBook() =
        CheckedOutBook(
            id = TEST_ID,
            book = getTestBook(),
            member = getTestMember(),
            issueDate = LocalDate.now(),
            returnDate = LocalDate.now().plusWeeks(1)
        )

    private fun getTestBookCheckedOutDto() =
        BookCheckedOutDto(
            bookId = TEST_ID,
            memberId = TEST_ID,
            issueDate = LocalDate.now(),
            returnDate = LocalDate.now().plusWeeks(1)
        )

    private fun getTestCheckoutDto() =
        CheckoutDto(
            memberId = TEST_ID,
            returnDate = LocalDate.now().plusWeeks(1)
        )

    private fun getTestBookReturnResultDto() =
        BookReturnResultDto(
            onTime = true,
            issueDate = LocalDate.now(),
            returnDate = LocalDate.now().plusWeeks(1)
        )

    private fun getTestMemberReferenceDto() =
        MemberReferenceDto(
            memberId = TEST_ID
        )
}
