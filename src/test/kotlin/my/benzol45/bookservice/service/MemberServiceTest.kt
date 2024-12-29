package my.benzol45.bookservice.service

import my.benzol45.bookservice.domain.Book
import my.benzol45.bookservice.domain.CheckedOutBook
import my.benzol45.bookservice.domain.Member
import my.benzol45.bookservice.mapper.BookMapper
import my.benzol45.bookservice.mapper.MemberMapper
import my.benzol45.bookservice.model.request.RegisterMemberDto
import my.benzol45.bookservice.model.response.BookDto
import my.benzol45.bookservice.model.response.MemberDto
import my.benzol45.bookservice.repository.MemberRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import java.util.*

@ExtendWith(MockitoExtension::class)
class MemberServiceTest {
    @Mock
    private lateinit var memberRepository: MemberRepository
    @Mock
    private lateinit var memberMapper: MemberMapper
    @Mock
    private lateinit var bookMapper: BookMapper
    @InjectMocks
    private lateinit var memberService: MemberService

    private val TEST_ID = 1L
    private val TEST_SURNAME = "Doe"
    private val TEST_EMAIL = "john.doe@example.com"
    private val TEST_PHONE = "1234567890"
    private val TEST_TITLE = "Test title"
    private val TEST_AUTHOR = "Test Author"
    private val TEST_ISBN = "123456789012"

    @Test
    fun `getAllMembers should return a page of member DTOs`() {
        // Arrange
        val pageable = PageRequest.of(0, 10)
        val member = getTestMember()
        val memberDto = getTestMemberDto()
        val page: Page<Member> = PageImpl(listOf(member), pageable, 1)

        Mockito.`when`(memberRepository.findAll(pageable)).thenReturn(page)
        Mockito.`when`(memberMapper.memberToMemberDTO(member)).thenReturn(memberDto)

        // Act
        val result = memberService.getAllMembers(pageable)

        // Assert
        assert(result.content.size == 1)
        assert(result.content[0].surname == TEST_SURNAME)
    }

    @Test
    fun `getFilteredMembers should return filtered members based on criteria`() {
        // Arrange
        val member = getTestMember()
        val memberDto = getTestMemberDto()
        val members = listOf(member)

        Mockito.`when`(memberRepository.findAllBySurnameAndEmailAndPhone(TEST_SURNAME, TEST_EMAIL, TEST_PHONE))
            .thenReturn(members)
        Mockito.`when`(memberMapper.memberToMemberDTO(member)).thenReturn(memberDto)

        // Act
        val result = memberService.getFilteredMembers(TEST_SURNAME, TEST_EMAIL, TEST_PHONE)

        // Assert
        assert(result[0].surname == TEST_SURNAME)
    }

    @Test
    fun `getMember should return a member DTO when member exists`() {
        // Arrange
        val member = getTestMember()
        val memberDto = getTestMemberDto()

        Mockito.`when`(memberRepository.findById(TEST_ID)).thenReturn(Optional.of(member))
        Mockito.`when`(memberMapper.memberToMemberDTO(member)).thenReturn(memberDto)

        // Act
        val result = memberService.getMember(TEST_ID)

        // Assert
        assert(result != null)
        assert(result?.surname == TEST_SURNAME)
    }

    @Test
    fun `getMemberBooks should return list of book DTOs for a member`() {
        // Arrange
        val book = getTestBook()
        val checkedOutBook = getTestCheckedOutBook()
        val member = getTestMember()
        member.checkedOutBooks = mutableListOf(checkedOutBook)
        val bookDto = getTestBookDto()

        Mockito.`when`(memberRepository.findById(TEST_ID)).thenReturn(Optional.of(member))
        Mockito.`when`(bookMapper.bookToBookDTO(book)).thenReturn(bookDto)

        val result = memberService.getMemberBooks(TEST_ID)

        // Assert
        assert(result.size == 1)
        assert(result[0].title == TEST_TITLE)
    }

    @Test
    fun `createMember should return the created member DTO`() {
        // Arrange
        val registerMemberDto = getTestRegisterMemberDto()
        val member = getTestMember()
        val memberDto = getTestMemberDto()

        Mockito.`when`(memberMapper.registerMemberDTOToMember(registerMemberDto)).thenReturn(member)
        Mockito.`when`(memberRepository.save(member)).thenReturn(member)
        Mockito.`when`(memberMapper.memberToMemberDTO(member)).thenReturn(memberDto)

        // Act
        val result = memberService.createMember(registerMemberDto)

        // Assert
        assert(result.surname == "Doe")
    }

    @Test
    fun `processBlockMember should return updated member DTO when block is updated`() {
        // Arrange
        val member = getTestMember()
        val updatedMember = member.apply { block = true }
        val memberDto = getTestBlockedMemberDto()


        Mockito.`when`(memberRepository.findById(TEST_ID)).thenReturn(Optional.of(member))
        Mockito.`when`(memberRepository.save(member)).thenReturn(updatedMember)
        Mockito.`when`(memberMapper.memberToMemberDTO(updatedMember)).thenReturn(memberDto)

        // Act
        val result = memberService.processBlockMember(TEST_ID, true)

        // Assert
        assert(result != null)
        assert(result?.block == true)
    }

    private fun getTestMember() =
        Member(
            id = TEST_ID,
            surname = TEST_SURNAME,
            email = TEST_EMAIL,
            phone = TEST_PHONE,
            block = false
        )

    private fun getTestMemberDto() =
        MemberDto(
            id = TEST_ID,
            name = "John",
            surname = TEST_SURNAME,
            email = TEST_EMAIL,
            phone = TEST_PHONE,
            block = false
        )

    private fun getTestBlockedMemberDto() =
        MemberDto(
            id = TEST_ID,
            name = "John",
            surname = TEST_SURNAME,
            email = TEST_EMAIL,
            phone = TEST_PHONE,
            block = true
        )

    private fun getTestRegisterMemberDto() =
        RegisterMemberDto(
            surname = TEST_SURNAME,
            email = TEST_EMAIL,
            phone = TEST_PHONE
        )

    private fun getTestBook() =
        Book(
            id = TEST_ID,
            title = TEST_TITLE,
            author = TEST_AUTHOR,
            isbn = TEST_ISBN)

    private fun getTestBookDto() =
        BookDto(
            id = TEST_ID,
            title = TEST_TITLE,
            author = TEST_AUTHOR,
            isbn = TEST_ISBN,
            pages = 100,
            publisher = null,
            dateOfPublication = null)

    private fun getTestCheckedOutBook() =
        CheckedOutBook(
            id = TEST_ID,
            book = getTestBook(),
            member = getTestMember(),
            issueDate = LocalDate.now(),
            returnDate = LocalDate.now().plusWeeks(1)
        )
}
