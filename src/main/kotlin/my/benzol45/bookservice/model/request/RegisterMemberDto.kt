package my.benzol45.bookservice.model.request

import jakarta.validation.constraints.NotBlank


data class RegisterMemberDto(
    val name: String? = null,
    @NotBlank
    val surname: String,
    val email: String? = null,
    val phone: String? = null,
)