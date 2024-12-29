package my.benzol45.bookservice.mapper

import my.benzol45.bookservice.domain.Book
import my.benzol45.bookservice.model.request.NewBookDto
import my.benzol45.bookservice.model.response.BookDto
import my.benzol45.bookservice.model.response.BookImportOpenlibraryDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface BookMapper {
    fun bookToBookDTO(book: Book): BookDto
    fun newBookDTOToBook(newBookDto: NewBookDto): Book

    @Mapping(target = "title", source = "bookImportOpenlibraryDto.title")
    @Mapping(target = "publisher", expression = "java(bookImportOpenlibraryDto.getPublishers().getFirst())")
    @Mapping(target = "dateOfPublication", expression = "java(LocalDate.of(Integer.parseInt(bookImportOpenlibraryDto.getPublish_date()), 1, 1))")
    fun bookImportOpenlibraryDtoToBookDto(bookImportOpenlibraryDto: BookImportOpenlibraryDto, isbn: String, author: String, pages:Int?, amount: Int): NewBookDto

}
