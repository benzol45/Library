package my.benzol45.bookservice.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class RestClientConfig {

    @Bean
    fun getRestClient(): RestClient =
        RestClient.create("https://openlibrary.org")

}