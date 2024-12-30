package my.benzol45.bookservice.service

import jakarta.transaction.Transactional
import lombok.RequiredArgsConstructor
import lombok.extern.log4j.Log4j
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
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@RequiredArgsConstructor
@Log4j
class CheckoutBookService(
    private val checkedOutBookRepository: CheckedOutBookRepository,
    private val bookRepository: BookRepository,
    private val availableBookRepository: AvailableBookRepository,
    private val memberRepository: MemberRepository,
    private val checkedOutBookMapper: CheckedOutBookMapper
) {

    @Transactional
    fun checkoutBook(id: Long, checkoutDto: CheckoutDto): BookCheckedOutDto? {
        val book:Book = bookRepository.findById(id).orElse(null) ?: return null
        val availableBook: AvailableBook = availableBookRepository.findFirstByBook(book) ?: return null
        if (availableBook.available <= 0) return null
        val member:Member = memberRepository.findById(checkoutDto.memberId).orElse(null) ?: return null

        with(availableBook) {
            available -= 1
            checkedOut += 1
            availableBookRepository.save(this)
        }
        return CheckedOutBook(
            book=book,
            member = member,
            issueDate = LocalDate.now(),
            returnDate = checkoutDto.returnDate)
        .let (checkedOutBookRepository::save)
        .let (checkedOutBookMapper::checkedOutBookToCheckedOutBookDTO)
    }

    @Transactional
    fun returnBook(id: Long, memberReferenceDto: MemberReferenceDto): BookReturnResultDto? {
        val book:Book = bookRepository.findById(id).orElse(null) ?: return null
        val availableBook: AvailableBook = availableBookRepository.findFirstByBook(book) ?: return null
        if (availableBook.checkedOut <= 0) return null
        val member:Member = memberRepository.findById(memberReferenceDto.memberId).orElse(null) ?: return null
        val checkedOutBook: CheckedOutBook = checkedOutBookRepository.findFirstByMemberAndBook(member, book)?: return null

        with(availableBook) {
            available += 1
            checkedOut -= 1
            availableBookRepository.save(this)
        }

        val bookReturnResultDto: BookReturnResultDto = checkedOutBookMapper.checkedOutBookToBookReturnResultDto(checkedOutBook)
        checkedOutBookRepository.delete(checkedOutBook)
        return bookReturnResultDto
    }

}