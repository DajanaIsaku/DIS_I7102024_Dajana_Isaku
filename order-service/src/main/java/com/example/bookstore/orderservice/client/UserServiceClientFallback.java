package com.example.bookstore.orderservice.client;

import com.example.bookstore.orderservice.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserServiceClientFallback implements UserServiceClient {

    @Override
    public UserDto getUserById(Long id) {
        log.warn("User service circuit breaker fallback triggered for user id: {}", id);
        throw new IllegalStateException("User service is temporarily unavailable");
    }
}
