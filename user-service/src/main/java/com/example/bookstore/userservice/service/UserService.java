package com.example.bookstore.userservice.service;

import com.example.bookstore.userservice.dto.CreateUserRequest;
import com.example.bookstore.userservice.dto.UserResponse;
import com.example.bookstore.userservice.entity.User;
import com.example.bookstore.userservice.exception.DuplicateResourceException;
import com.example.bookstore.userservice.exception.ResourceNotFoundException;
import com.example.bookstore.userservice.mapper.UserMapper;
import com.example.bookstore.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User with email already exists: " + request.getEmail());
        }
        User saved = userRepository.save(UserMapper.toEntity(request));
        return UserMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponse)
                .toList();
    }
}
