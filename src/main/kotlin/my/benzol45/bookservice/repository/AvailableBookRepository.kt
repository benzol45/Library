package my.benzol45.bookservice.repository

import my.benzol45.bookservice.domain.AvailableBook
import my.benzol45.bookservice.domain.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AvailableBookRepository: JpaRepository<AvailableBook, Long> {
    fun findFirstByBook(book: Book): AvailableBook?
}