package com.example.bookstore.bookservice.mapper;

import com.example.bookstore.bookservice.dto.BookResponse;
import com.example.bookstore.bookservice.dto.CreateBookRequest;
import com.example.bookstore.bookservice.entity.Book;

public final class BookMapper {

    private BookMapper() {
    }

    public static Book toEntity(CreateBookRequest request) {
        return Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .price(request.getPrice())
                .availableQuantity(request.getAvailableQuantity())
                .build();
    }

    public static BookResponse toResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .price(book.getPrice())
                .availableQuantity(book.getAvailableQuantity())
                .build();
    }
}
