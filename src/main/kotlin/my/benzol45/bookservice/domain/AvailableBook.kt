package my.benzol45.bookservice.domain

import jakarta.persistence.*

@Entity
@Table(name = "available_books")
data class AvailableBook(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false)
    var book: Book,

    @Column(name = "amount", nullable = false)
    var amount: Int = 0,

    @Column(name = "checked_out", nullable = false)
    var checkedOut: Int = 0,

    @Column(name = "available", nullable = false)
    var available: Int = 0
)