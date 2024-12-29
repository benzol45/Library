package my.benzol45.bookservice.model.response

data class BookImportOpenlibraryDto(
    val authors: List<Author>,
    val pagination: String,
    val publish_date: String,
    val publishers: List<String>,
    val title: String
)

data class Author(
    val key: String
)

data class AuthorDTO(
    val name: String
)


