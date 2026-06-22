package com.example.bookstore.bookservice.config;

import com.example.bookstore.bookservice.dto.CreateBookRequest;
import com.example.bookstore.bookservice.repository.BookRepository;
import com.example.bookstore.bookservice.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataLoader {

    private final BookService bookService;
    private final BookRepository bookRepository;

    @Bean
    @Profile("!test")
    CommandLineRunner loadSampleBooks() {
        return args -> {
            if (bookRepository.count() > 0) {
                return;
            }
            bookService.createBook(CreateBookRequest.builder()
                    .title("Clean Code")
                    .author("Robert C. Martin")
                    .isbn("978-0132350884")
                    .price(new BigDecimal("39.99"))
                    .availableQuantity(50)
                    .build());
            bookService.createBook(CreateBookRequest.builder()
                    .title("Effective Java")
                    .author("Joshua Bloch")
                    .isbn("978-0134685991")
                    .price(new BigDecimal("45.00"))
                    .availableQuantity(30)
                    .build());
            log.info("Sample books initialized");
        };
    }
}
