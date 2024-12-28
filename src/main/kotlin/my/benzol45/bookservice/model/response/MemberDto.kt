package my.benzol45.bookservice.model.response

data class MemberDto(
    val id : Long,
    val name: String?,
    val surname: String,
    val email: String?,
    val phone: String?,
    val block: Boolean
)