package com.example.bookstore.userservice.config;

import com.example.bookstore.userservice.dto.CreateUserRequest;
import com.example.bookstore.userservice.repository.UserRepository;
import com.example.bookstore.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataLoader {

    private final UserService userService;
    private final UserRepository userRepository;

    @Bean
    @Profile("!test")
    CommandLineRunner loadSampleUsers() {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }
            userService.createUser(CreateUserRequest.builder()
                    .firstName("Alice")
                    .lastName("Johnson")
                    .email("alice@bookstore.com")
                    .phoneNumber("+1234567890")
                    .build());
            userService.createUser(CreateUserRequest.builder()
                    .firstName("Bob")
                    .lastName("Smith")
                    .email("bob@bookstore.com")
                    .phoneNumber("+0987654321")
                    .build());
            log.info("Sample users initialized");
        };
    }
}
