package my.benzol45.bookservice.model

import java.time.LocalDate

data class BookDto(
    val id: Long? = null,
    var isbn: String,
    var author: String? = null,
    var title: String,
    var pages: Int? = null,
    var publisher: String? = null,
    var dateOfPublication: LocalDate? = null
)