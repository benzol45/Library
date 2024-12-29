package my.benzol45.bookservice.repository

import my.benzol45.bookservice.domain.Book
import my.benzol45.bookservice.domain.CheckedOutBook
import my.benzol45.bookservice.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CheckedOutBookRepository: JpaRepository<CheckedOutBook, Long> {
    fun findFirstByMemberAndBook(member: Member, book: Book): CheckedOutBook?
}