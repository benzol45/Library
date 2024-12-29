package my.benzol45.bookservice.model.response

import java.time.LocalDate

data class BookReturnResultDto(
    val onTime: Boolean,
    val issueDate: LocalDate,
    val returnDate: LocalDate
)