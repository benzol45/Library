package my.benzol45.bookservice.model.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.time.LocalDate

data class NewBookDto(
    @field:NotBlank
    val isbn: String,
    val author: String? = null,
    @field:NotBlank
    val title: String,
    val pages: Int? = null,
    val publisher: String? = null,
    val dateOfPublication: LocalDate? = null,
    @field:Positive
    val amount: Int
)