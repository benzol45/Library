package my.benzol45.bookservice.mapper

import my.benzol45.bookservice.domain.CheckedOutBook
import my.benzol45.bookservice.model.response.BookCheckedOutDto
import my.benzol45.bookservice.model.response.BookReturnResultDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface CheckedOutBookMapper {
    @Mapping(target = "bookId", source = "checkedOutBook.book.id")
    @Mapping(target = "memberId", source = "checkedOutBook.member.id")
    fun checkedOutBookToCheckedOutBookDTO(checkedOutBook: CheckedOutBook): BookCheckedOutDto

    @Mapping(target = "onTime", expression = "java(checkedOutBook.getReturnDate().isBefore(LocalDate.now()))")
    fun checkedOutBookToBookReturnResultDto(checkedOutBook: CheckedOutBook): BookReturnResultDto
}