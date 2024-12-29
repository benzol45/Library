package my.benzol45.bookservice.model.response

import java.time.LocalDate

data class BookDto(
    val id: Long,
    val isbn: String,
    val author: String?,
    val title: String,
    val pages: Int?,
    val publisher: String?,
    val dateOfPublication: LocalDate?
)