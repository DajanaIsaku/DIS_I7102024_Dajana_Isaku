package com.example.bookstore.orderservice.service;

import com.example.bookstore.orderservice.client.BookServiceClient;
import com.example.bookstore.orderservice.client.UserServiceClient;
import com.example.bookstore.orderservice.dto.BookDto;
import com.example.bookstore.orderservice.dto.CreateOrderRequest;
import com.example.bookstore.orderservice.dto.OrderResponse;
import com.example.bookstore.orderservice.dto.UserDto;
import com.example.bookstore.orderservice.event.OrderCreatedEvent;
import com.example.bookstore.orderservice.exception.InvalidOrderException;
import com.example.bookstore.orderservice.messaging.OrderEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private com.example.bookstore.orderservice.repository.OrderRepository orderRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private BookServiceClient bookServiceClient;

    @Mock
    private OrderEventPublisher orderEventPublisher;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_shouldValidateUserAndBookAndPublishEvent() {
        when(userServiceClient.getUserById(1L)).thenReturn(UserDto.builder().id(1L).email("a@test.com").build());
        when(bookServiceClient.getBookById(2L)).thenReturn(BookDto.builder()
                .id(2L)
                .price(new BigDecimal("10.00"))
                .availableQuantity(5)
                .build());
        when(orderRepository.save(any())).thenAnswer(invocation -> {
            com.example.bookstore.orderservice.entity.Order order = invocation.getArgument(0);
            order.setId(100L);
            return order;
        });

        CreateOrderRequest request = CreateOrderRequest.builder()
                .userId(1L)
                .bookId(2L)
                .quantity(2)
                .build();

        OrderResponse response = orderService.createOrder(request);

        assertThat(response.getTotalPrice()).isEqualByComparingTo("20.00");
        assertThat(response.getId()).isEqualTo(100L);

        ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(orderEventPublisher).publishOrderCreated(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getOrderId()).isEqualTo(100L);
    }

    @Test
    void createOrder_insufficientStock_shouldThrow() {
        when(userServiceClient.getUserById(1L)).thenReturn(UserDto.builder().id(1L).build());
        when(bookServiceClient.getBookById(2L)).thenReturn(BookDto.builder()
                .id(2L)
                .price(new BigDecimal("10.00"))
                .availableQuantity(1)
                .build());

        CreateOrderRequest request = CreateOrderRequest.builder()
                .userId(1L)
                .bookId(2L)
                .quantity(5)
                .build();

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(InvalidOrderException.class);
    }
}
