package com.example.bookstore.orderservice.service;

import com.example.bookstore.orderservice.client.BookServiceClient;
import com.example.bookstore.orderservice.client.UserServiceClient;
import com.example.bookstore.orderservice.dto.BookDto;
import com.example.bookstore.orderservice.dto.CreateOrderRequest;
import com.example.bookstore.orderservice.dto.OrderResponse;
import com.example.bookstore.orderservice.dto.UserDto;
import com.example.bookstore.orderservice.entity.Order;
import com.example.bookstore.orderservice.entity.OrderStatus;
import com.example.bookstore.orderservice.event.OrderCreatedEvent;
import com.example.bookstore.orderservice.exception.InvalidOrderException;
import com.example.bookstore.orderservice.exception.ResourceNotFoundException;
import com.example.bookstore.orderservice.mapper.OrderMapper;
import com.example.bookstore.orderservice.messaging.OrderEventPublisher;
import com.example.bookstore.orderservice.repository.OrderRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserServiceClient userServiceClient;
    private final BookServiceClient bookServiceClient;
    private final OrderEventPublisher orderEventPublisher;

    public OrderResponse createOrder(CreateOrderRequest request) {
        UserDto user = fetchUser(request.getUserId());
        BookDto book = fetchBook(request.getBookId());

        if (book.getAvailableQuantity() < request.getQuantity()) {
            throw new InvalidOrderException("Insufficient stock for book id: " + request.getBookId());
        }

        BigDecimal totalPrice = book.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        Order order = Order.builder()
                .userId(user.getId())
                .bookId(book.getId())
                .quantity(request.getQuantity())
                .totalPrice(totalPrice)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        Order saved = orderRepository.save(order);

        orderEventPublisher.publishOrderCreated(OrderCreatedEvent.builder()
                .orderId(saved.getId())
                .userId(saved.getUserId())
                .bookId(saved.getBookId())
                .quantity(saved.getQuantity())
                .totalPrice(saved.getTotalPrice())
                .createdAt(saved.getCreatedAt())
                .build());

        return OrderMapper.toResponse(saved);
    }

    UserDto fetchUser(Long userId) {
        try {
            return userServiceClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            throw new InvalidOrderException("User not found with id: " + userId);
        }
    }

    BookDto fetchBook(Long bookId) {
        try {
            return bookServiceClient.getBookById(bookId);
        } catch (FeignException.NotFound e) {
            throw new InvalidOrderException("Book not found with id: " + bookId);
        }
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return OrderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderMapper::toResponse)
                .toList();
    }
}
