package my.benzol45.bookservice.controller

import lombok.RequiredArgsConstructor
import my.benzol45.bookservice.domain.Book
import my.benzol45.bookservice.service.BookService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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
    ): Page<Book> = bookService.getAllBooks(PageRequest.of(page,size))

    @GetMapping("/filter")
    fun getFilteredBooks(
        @RequestParam(name = "title", required = false) title: String?,
        @RequestParam(name = "author", required = false) author: String?
    ): List<Book> = bookService.getFilteredBooks(title, author)
}