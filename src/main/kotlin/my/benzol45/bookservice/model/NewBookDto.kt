package my.benzol45.bookservice.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.time.LocalDate

data class NewBookDto(
    @NotBlank
    val isbn: String,
    val author: String? = null,
    @NotBlank
    val title: String,
    val pages: Int? = null,
    val publisher: String? = null,
    val dateOfPublication: LocalDate? = null,
    @Positive
    val amount: Int
)