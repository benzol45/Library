package my.benzol45.bookservice.service

import jakarta.transaction.Transactional
import my.benzol45.bookservice.domain.AvailableBook
import my.benzol45.bookservice.domain.Book
import my.benzol45.bookservice.mapper.BookMapper
import my.benzol45.bookservice.model.request.BookImportDto
import my.benzol45.bookservice.model.request.NewBookDto
import my.benzol45.bookservice.model.response.BookDto
import my.benzol45.bookservice.repository.AvailableBookRepository
import my.benzol45.bookservice.repository.BookRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val availableBookRepository: AvailableBookRepository,
    private val bookMapper: BookMapper,
    private val bookImportService: BookImportService
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

    @Transactional
    fun importBook(bookImportDto: BookImportDto): BookDto? =
        bookRepository.getFirstByIsbn(bookImportDto.isbn)
            ?.let { return bookMapper.bookToBookDTO(it) }
            ?: let { bookImportService.importBook(bookImportDto) }
                ?.let { newBookDto -> createBook(newBookDto) }

    @Transactional
    fun createBook(newBookDto: NewBookDto): BookDto {
        val book:Book = bookRepository.getFirstByIsbn(newBookDto.isbn)
            ?:let {
                bookMapper.newBookDTOToBook(newBookDto)
                .let(bookRepository::save)
            }

        val availableBook:AvailableBook = availableBookRepository.findFirstByBook(book) ?: AvailableBook(book=book)
        availableBook.amount += newBookDto.amount
        availableBook.available += newBookDto.amount
        availableBookRepository.save(availableBook)

        return bookMapper.bookToBookDTO(book)
    }

    fun deleteBook(id: Long) =
        bookRepository.deleteById(id)

}