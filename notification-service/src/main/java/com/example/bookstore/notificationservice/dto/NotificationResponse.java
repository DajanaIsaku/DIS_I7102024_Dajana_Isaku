package com.example.bookstore.notificationservice.dto;

import com.example.bookstore.notificationservice.entity.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;
    private Long userId;
    private Long orderId;
    private String message;
    private NotificationStatus status;
    private LocalDateTime createdAt;
}
