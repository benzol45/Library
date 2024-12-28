package my.benzol45.bookservice.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "books")
data class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "isbn", unique = true, nullable = false)
    var isbn: String,

    @Column(name = "author", nullable = true)
    var author: String? = null,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "pages", nullable = true)
    var pages: Int? = null,

    @Column(name = "publisher", nullable = true)
    var publisher: String? = null,

    @Column(name = "date_of_publication")
    var dateOfPublication: LocalDate? = null
)