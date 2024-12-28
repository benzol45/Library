package my.benzol45.bookservice.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class BookImportDto(
    @NotBlank
    val isbn: String,
    @Positive
    val amount: Int)