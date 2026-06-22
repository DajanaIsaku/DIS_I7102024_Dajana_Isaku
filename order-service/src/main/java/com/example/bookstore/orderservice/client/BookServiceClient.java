package com.example.bookstore.orderservice.client;

import com.example.bookstore.orderservice.dto.BookDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "book-service", path = "/books", fallback = BookServiceClientFallback.class)
public interface BookServiceClient {

    @GetMapping("/{id}")
    BookDto getBookById(@PathVariable("id") Long id);
}
