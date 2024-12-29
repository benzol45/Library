package my.benzol45.bookservice.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "checked_out_books")
data class CheckedOutBook(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false)
    var book: Book,

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    var member: Member,

    @Column(name = "issue_date")
    var issueDate: LocalDate,

    @Column(name = "return_date")
    var returnDate: LocalDate

)