package com.polarbookshop.orderservice.book;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class BookClient {

    private static final String BOOKS_ROOT_API = "/books/";

    /**
     * WebClient is a reactive HTTP client
     */
    private final WebClient webClient;

    public Mono<Book> getBookByIsbn(String isbn) {
        return webClient
                .get()
                .uri(BOOKS_ROOT_API + isbn)
                .retrieve()
                .bodyToMono(Book.class)
                // Defining timeout and fallback for the HTTP interaction (The fallback returns an empty Mono object.)
                .timeout(Duration.ofSeconds(3), Mono.empty())
                ;
    }

}
