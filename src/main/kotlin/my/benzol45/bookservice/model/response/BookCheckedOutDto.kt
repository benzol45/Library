package my.benzol45.bookservice.model.response

import java.time.LocalDate

data class BookCheckedOutDto(
    val bookId: Long,
    val memberId: Long,
    val issueDate: LocalDate,
    val returnDate: LocalDate
)