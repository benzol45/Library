package my.benzol45.bookservice.service.impl

import com.fasterxml.jackson.core.JsonProcessingException
import lombok.RequiredArgsConstructor
import my.benzol45.bookservice.mapper.BookMapper
import my.benzol45.bookservice.model.request.BookImportDto
import my.benzol45.bookservice.model.request.NewBookDto
import my.benzol45.bookservice.model.response.AuthorDTO
import my.benzol45.bookservice.model.response.BookImportOpenlibraryDto
import my.benzol45.bookservice.service.BookImportService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient

@Service
@RequiredArgsConstructor
class BookImportServiceOpenLibraryImpl(
    private val restClient: RestClient,
    private val bookMapper: BookMapper
) : BookImportService {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun importBook(bookImportDto: BookImportDto): NewBookDto? {
        log.info("Request book info from Openlibrary for isbn: ${bookImportDto.isbn}")
        try {
            val response: ResponseEntity<Unit> = restClient
                .get()
                .uri("/isbn/${bookImportDto.isbn}.json")
                .retrieve()
                .toEntity(Unit::class.java)

            if (!response.statusCode.equals(HttpStatus.FOUND)) {
                log.warn("Not found book info from Openlibrary. Code ${response.statusCode.value()}")
                return null
            }

            val responseBook: ResponseEntity<BookImportOpenlibraryDto> = restClient
                .get()
                .uri(response.headers.location!!)
                .retrieve()
                .toEntity(BookImportOpenlibraryDto::class.java)

            if (!responseBook.statusCode.is2xxSuccessful) {
                log.warn("Response isn't OK for getting book info from Openlibrary. Code ${responseBook.statusCode.value()}")
                return null
            }

            val bookImportOpenlibraryDto: BookImportOpenlibraryDto = responseBook.body!!

            val author: String = getAuthor(bookImportOpenlibraryDto)

            return bookMapper.bookImportOpenlibraryDtoToBookDto(
                bookImportOpenlibraryDto,
                bookImportDto.isbn,
                author,
                getPageNumber(bookImportOpenlibraryDto),
                bookImportDto.amount)

        } catch (ex: HttpClientErrorException) {
            log.warn("HTTP error for getting book info from Openlibrary: ${ex.message}")
            return null
        } catch (ex: JsonProcessingException) {
            log.warn("Incorrect json response for getting book info from Openlibrary: ${ex.message}")
            return null
        }
    }

    private fun getAuthor(bookImportOpenlibraryDto: BookImportOpenlibraryDto): String {
        val authors: String = bookImportOpenlibraryDto.authors
            .map {
                restClient
                    .get()
                    .uri("${it.key}.json")
                    .retrieve()
                    .toEntity(AuthorDTO::class.java)
            }
            .mapNotNull {
                if (it.statusCode.is2xxSuccessful) {
                    it.body?.name
                } else {
                    null
                }
            }
            .joinToString(separator = ", ")
        return authors
    }

    private fun getPageNumber(bookImportOpenlibraryDto: BookImportOpenlibraryDto): Int? {
        val regex: Regex = "^\\d+".toRegex()
        val matchResult: MatchResult? = regex.find(bookImportOpenlibraryDto.pagination)
        return matchResult?.value?.toInt()
    }
}