package my.benzol45.bookservice.model.request

import jakarta.validation.constraints.Future
import java.time.LocalDate

data class CheckoutDto(
    val memberId: Long,
    @field:Future
    val returnDate: LocalDate
)