package com.example.bookstore.orderservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreatedEvent {

    private Long orderId;
    private Long userId;
    private Long bookId;
    private Integer quantity;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
}
