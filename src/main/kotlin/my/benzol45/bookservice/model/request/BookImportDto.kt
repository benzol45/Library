package my.benzol45.bookservice.model.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class BookImportDto(
    @field:NotBlank
    val isbn: String,
    @field:Positive
    val amount: Int)