package my.benzol45.bookservice.repository

import my.benzol45.bookservice.domain.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface BookRepository: JpaRepository<Book, Long> {
    @Query("""
        SELECT * FROM books b
        WHERE 
            (:title IS NULL OR b.title ILIKE CONCAT('%', :title, '%'))
        AND 
            (:author IS NULL OR b.author ILIKE CONCAT('%', :author, '%'))
    """, nativeQuery = true)
    fun findAllByTitleAndAuthor(title: String?, author: String?): List<Book>

}