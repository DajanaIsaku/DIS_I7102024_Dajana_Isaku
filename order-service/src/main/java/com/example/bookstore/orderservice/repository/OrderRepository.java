package com.example.bookstore.orderservice.repository;

import com.example.bookstore.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
