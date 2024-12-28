package my.benzol45.bookservice.mapper

import my.benzol45.bookservice.domain.Book
import my.benzol45.bookservice.model.BookDto
import my.benzol45.bookservice.model.NewBookDto
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface BookMapper {
    fun bookToBookDTO(book: Book): BookDto
    fun newBookDTOToBook(newBookDto: NewBookDto): Book
}