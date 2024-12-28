package my.benzol45.bookservice.service

import jakarta.transaction.Transactional
import lombok.RequiredArgsConstructor
import lombok.extern.log4j.Log4j
import my.benzol45.bookservice.domain.AvailableBook
import my.benzol45.bookservice.domain.Book
import my.benzol45.bookservice.mapper.BookMapper
import my.benzol45.bookservice.model.response.BookDto
import my.benzol45.bookservice.model.request.BookImportDto
import my.benzol45.bookservice.model.request.NewBookDto
import my.benzol45.bookservice.repository.AvailableBookRepository
import my.benzol45.bookservice.repository.BookRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
@Log4j
class BookService(
    private val bookRepository: BookRepository,
    private val availableBookRepository: AvailableBookRepository,
    private val bookMapper: BookMapper
) {

    fun getAllBooks(pageable: Pageable): Page<BookDto> =
        bookRepository.findAll(pageable)
            .map(bookMapper::bookToBookDTO)

    fun getFilteredBooks(title: String?, author: String?): List<BookDto> =
        bookRepository.findAllByTitleAndAuthor(title, author)
            .map(bookMapper::bookToBookDTO)

    fun getBook(id: Long): BookDto? =
        bookRepository.findById(id).orElse(null)
        ?.let(bookMapper::bookToBookDTO)

    fun importBook(bookImportDto: BookImportDto): BookDto? =  TODO()

    @Transactional
    fun createBook(newBookDto: NewBookDto): BookDto {
        val book:Book = bookRepository.getFirstByIsbn(newBookDto.isbn)
            ?:let {
                bookMapper.newBookDTOToBook(newBookDto)
                .let(bookRepository::save)
            }

        val availableBook:AvailableBook = availableBookRepository.findFirstByBook(book) ?: AvailableBook(book=book)
        availableBook.amount += newBookDto.amount
        availableBookRepository.save(availableBook)

        return bookMapper.bookToBookDTO(book)
    }

    fun deleteBook(id: Long) =
        bookRepository.deleteById(id)

}