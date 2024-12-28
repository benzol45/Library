package my.benzol45.bookservice.controller

import lombok.RequiredArgsConstructor
import my.benzol45.bookservice.model.BookDto
import my.benzol45.bookservice.model.BookImportDto
import my.benzol45.bookservice.model.NewBookDto
import my.benzol45.bookservice.service.BookService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
class BookController (
    private val bookService: BookService
){
    @GetMapping
    fun getAllBooks(
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "20") size: Int
    ): Page<BookDto> =
        bookService.getAllBooks(PageRequest.of(page,size))

    @GetMapping("/filter")
    fun getFilteredBooks(
        @RequestParam(name = "title", required = false) title: String?,
        @RequestParam(name = "author", required = false) author: String?
    ): List<BookDto> =
        bookService.getFilteredBooks(title, author)

    @PostMapping("/import")
    fun importBook(@RequestBody book: BookImportDto): ResponseEntity<BookDto> =
        bookService.importBook(book)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createBook(@RequestBody newBookDto: NewBookDto): BookDto =
        bookService.createBook(newBookDto)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBook(@PathVariable("id") id: Long) =  bookService.deleteBook(id)

}