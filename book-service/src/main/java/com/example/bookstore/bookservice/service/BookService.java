package com.example.bookstore.bookservice.service;

import com.example.bookstore.bookservice.dto.BookResponse;
import com.example.bookstore.bookservice.dto.CreateBookRequest;
import com.example.bookstore.bookservice.dto.UpdateStockRequest;
import com.example.bookstore.bookservice.entity.Book;
import com.example.bookstore.bookservice.exception.DuplicateResourceException;
import com.example.bookstore.bookservice.exception.ResourceNotFoundException;
import com.example.bookstore.bookservice.mapper.BookMapper;
import com.example.bookstore.bookservice.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    public BookResponse createBook(CreateBookRequest request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateResourceException("Book with ISBN already exists: " + request.getIsbn());
        }
        Book saved = bookRepository.save(BookMapper.toEntity(request));
        return BookMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return BookMapper.toResponse(book);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookMapper::toResponse)
                .toList();
    }

    public BookResponse updateStock(Long id, UpdateStockRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        book.setAvailableQuantity(request.getAvailableQuantity());
        return BookMapper.toResponse(bookRepository.save(book));
    }

    public void decreaseStock(Long id, int quantity) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        if (book.getAvailableQuantity() < quantity) {
            throw new IllegalStateException("Insufficient stock for book id: " + id);
        }
        book.setAvailableQuantity(book.getAvailableQuantity() - quantity);
        bookRepository.save(book);
    }
}
