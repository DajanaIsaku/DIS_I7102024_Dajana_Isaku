package com.example.bookstore.orderservice.mapper;

import com.example.bookstore.orderservice.dto.OrderResponse;
import com.example.bookstore.orderservice.entity.Order;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .bookId(order.getBookId())
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
