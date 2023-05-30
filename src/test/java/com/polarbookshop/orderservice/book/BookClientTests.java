package com.polarbookshop.orderservice.book;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

public class BookClientTests {

    /**
     * The OkHttp project provides a mock web server that
     * you can use to test HTTP-based request/response interactions with a service downstream.
     */
    private MockWebServer mockWebServer;

    /**
     * BookClient returns a Mono<Book> object, so
     * you can use the convenient utilities provided by Project Reactor for testing reactive applications.
     */
    private BookClient bookClient;

    @BeforeEach
    void setup() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        this.bookClient = new BookClient(WebClient.builder()
                .baseUrl(mockWebServer.url("/").uri().toString())
                .build()
        );
    }

    @AfterEach
    void clean() throws IOException {
        this.mockWebServer.shutdown();
    }

    @Test
    void whenBookExistsThenReturnBook() {
        var bookIsbn = "123567890";

        var mockResponse = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                        {
                          "isbn": %s,
                          "title": "Title",
                          "author": "Author",
                          "price": 9.90,
                          "publisher": "Polarsophia"
                        }
                        """.formatted(bookIsbn));

        mockWebServer.enqueue(mockResponse);

        Mono<Book> book = bookClient.getBookByIsbn(bookIsbn);

        // The StepVerifier object lets you process reactive streams and write assertions in steps through a fluent API.
        StepVerifier.create(book)
                .expectNextMatches(b -> b.isbn().equals(bookIsbn))
                .verifyComplete();
    }

}
