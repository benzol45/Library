package my.benzol45.bookservice.controller

import lombok.RequiredArgsConstructor
import my.benzol45.bookservice.model.response.BookDto
import my.benzol45.bookservice.model.request.BookImportDto
import my.benzol45.bookservice.model.request.CheckoutDto
import my.benzol45.bookservice.model.request.MemberReferenceDto
import my.benzol45.bookservice.model.request.NewBookDto
import my.benzol45.bookservice.model.response.BookCheckedOutDto
import my.benzol45.bookservice.model.response.BookReturnResultDto
import my.benzol45.bookservice.service.BookService
import my.benzol45.bookservice.service.CheckoutBookService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
class BookController (
    private val bookService: BookService,
    private val checkoutBookService: CheckoutBookService
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

    @GetMapping("/{id}")
    fun getBook(@PathVariable("id") id: Long): ResponseEntity<BookDto> =
        bookService.getBook(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

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

    @PatchMapping("/{id}/checkout")
    fun checkoutBook(
        @PathVariable("id") id: Long,
        @RequestBody checkoutDto: CheckoutDto
    ): ResponseEntity<BookCheckedOutDto>  =
        checkoutBookService.checkoutBook(id, checkoutDto)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PatchMapping("/{id}/return")
    fun returnBook(
        @PathVariable("id") id: Long,
        @RequestBody memberRefDto: MemberReferenceDto
    ): ResponseEntity<BookReturnResultDto>  =
        checkoutBookService.returnBook(id, memberRefDto)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

}