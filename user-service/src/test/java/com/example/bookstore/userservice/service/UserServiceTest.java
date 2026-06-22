package com.example.bookstore.userservice.service;

import com.example.bookstore.userservice.dto.CreateUserRequest;
import com.example.bookstore.userservice.dto.UserResponse;
import com.example.bookstore.userservice.exception.DuplicateResourceException;
import com.example.bookstore.userservice.exception.ResourceNotFoundException;
import com.example.bookstore.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser_shouldPersistUser() {
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane@example.com")
                .phoneNumber("555-0100")
                .build();

        UserResponse response = userService.createUser(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getEmail()).isEqualTo("jane@example.com");
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void createUser_duplicateEmail_shouldThrow() {
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("duplicate@example.com")
                .build();
        userService.createUser(request);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void getUserById_notFound_shouldThrow() {
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
