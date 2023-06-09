package com.polarbookshop.orderservice.book;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

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
                // Returns an empty object when a 404 response is received
                .onErrorResume(WebClientResponseException.NotFound.class, exception -> Mono.empty())
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
                // If any error happens after the 3 retry attempts, catch the exception and return an empty object.
                .onErrorResume(Exception.class, exception -> Mono.empty())
                ;
    }

}
