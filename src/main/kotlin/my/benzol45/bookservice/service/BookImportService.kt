package my.benzol45.bookservice.service

import my.benzol45.bookservice.model.request.BookImportDto
import my.benzol45.bookservice.model.request.NewBookDto

interface BookImportService {
    fun importBook(bookImportDto: BookImportDto): NewBookDto?
}