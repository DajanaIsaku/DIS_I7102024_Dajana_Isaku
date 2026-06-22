package com.example.bookstore.bookservice.service;

import com.example.bookstore.bookservice.dto.CreateBookRequest;
import com.example.bookstore.bookservice.dto.UpdateStockRequest;
import com.example.bookstore.bookservice.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Test
    void createBook_shouldPersistBook() {
        var response = bookService.createBook(CreateBookRequest.builder()
                .title("Microservices Patterns")
                .author("Chris Richardson")
                .isbn("978-1617294549")
                .price(new BigDecimal("49.99"))
                .availableQuantity(10)
                .build());

        assertThat(response.getId()).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Microservices Patterns");
    }

    @Test
    void updateStock_shouldUpdateQuantity() {
        var created = bookService.createBook(CreateBookRequest.builder()
                .title("Domain-Driven Design")
                .author("Eric Evans")
                .isbn("978-0321125217")
                .price(new BigDecimal("59.99"))
                .availableQuantity(5)
                .build());

        var updated = bookService.updateStock(created.getId(), UpdateStockRequest.builder()
                .availableQuantity(20)
                .build());

        assertThat(updated.getAvailableQuantity()).isEqualTo(20);
    }

    @Test
    void getBookById_notFound_shouldThrow() {
        assertThatThrownBy(() -> bookService.getBookById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
