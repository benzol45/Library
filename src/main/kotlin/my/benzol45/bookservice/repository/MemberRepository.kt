package my.benzol45.bookservice.repository

import my.benzol45.bookservice.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository: JpaRepository<Member, Long> {
    @Query("""
        SELECT * FROM members m
        WHERE 
            (:surname IS NULL OR m.surname ILIKE CONCAT('%', :surname, '%'))
        AND 
            (:email IS NULL OR m.email ILIKE CONCAT('%', :email, '%'))
        AND 
            (:phone IS NULL OR m.phone ILIKE CONCAT('%', :phone, '%'))
    """, nativeQuery = true)
    fun findAllBySurnameAndEmailAndPhone(surname: String?, email: String?, phone: String?): List<Member>
}