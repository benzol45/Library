package my.benzol45.bookservice.service

import lombok.RequiredArgsConstructor
import my.benzol45.bookservice.domain.Book
import my.benzol45.bookservice.repository.BookRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class BookService(
    private val bookRepository: BookRepository
) {
    fun getAllBooks(pageable: Pageable): Page<Book> = bookRepository.findAll(pageable)

    fun getAllAvailableBooks(): List<Book> = TODO()

    fun getFilteredBooks(title: String?, author: String?): List<Book> = bookRepository.findAllByTitleAndAuthor(title, author)

}