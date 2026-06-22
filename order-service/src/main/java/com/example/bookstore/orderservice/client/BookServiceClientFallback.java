package com.example.bookstore.orderservice.client;

import com.example.bookstore.orderservice.dto.BookDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookServiceClientFallback implements BookServiceClient {

    @Override
    public BookDto getBookById(Long id) {
        log.warn("Book service circuit breaker fallback triggered for book id: {}", id);
        throw new IllegalStateException("Book service is temporarily unavailable");
    }
}
