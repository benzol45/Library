package my.benzol45.bookservice.domain

import jakarta.persistence.*

@Entity
@Table(name = "members")
data class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name", nullable = true)
    var name: String? = null,

    @Column(name = "surname", nullable = false)
    var surname: String,

    @Column(name = "email",  nullable = true)
    var email: String? = null,

    @Column(name = "phone", nullable = true)
    var phone: String? = null,

    @Column(name = "block", nullable = false)
    var block: Boolean,

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    var checkedOutBooks: MutableList<CheckedOutBook> = mutableListOf()
)