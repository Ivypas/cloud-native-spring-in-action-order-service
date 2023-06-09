package com.polarbookshop.orderservice.order.domain;

import com.polarbookshop.orderservice.book.Book;
import com.polarbookshop.orderservice.book.BookClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final BookClient bookClient;

    private final OrderRepository orderRepository;

    public static Order buildAcceptedOrder(Book book, int quantity) {
        return Order.of(
                book.isbn(),
                String.format("%s - %s", book.title(), book.author()),
                book.price(),
                quantity,
                OrderStatus.ACCEPTED
        );
    }

    public static Order buildRejectedOrder(String bookIsbn, int quantity) {
        return Order.of(
                bookIsbn,
                null,
                null,
                quantity,
                OrderStatus.REJECTED
        );
    }

    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * bookClient 查不到 isbn 会返回 Mono.empty()
     * <p>
     * 收到 Mono.empty() 会 buidRejectedOrder(isbn, quantity)
     *
     * @param isbn
     * @param quantity
     * @return
     */
    public Mono<Order> submitOrder(String isbn, int quantity) {
        return bookClient.getBookByIsbn(isbn)
                .map(book -> buildAcceptedOrder(book, quantity))
                .defaultIfEmpty(buildRejectedOrder(isbn, quantity))
                .flatMap(orderRepository::save);
    }

}
