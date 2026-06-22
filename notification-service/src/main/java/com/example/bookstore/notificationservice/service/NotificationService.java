package com.example.bookstore.notificationservice.service;

import com.example.bookstore.notificationservice.dto.NotificationResponse;
import com.example.bookstore.notificationservice.entity.Notification;
import com.example.bookstore.notificationservice.entity.NotificationStatus;
import com.example.bookstore.notificationservice.event.OrderCreatedEvent;
import com.example.bookstore.notificationservice.event.PaymentCompletedEvent;
import com.example.bookstore.notificationservice.exception.ResourceNotFoundException;
import com.example.bookstore.notificationservice.mapper.NotificationMapper;
import com.example.bookstore.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void handleOrderCreated(OrderCreatedEvent event) {
        String message = String.format(
                "Your order #%d has been created. Total amount: %s",
                event.getOrderId(),
                event.getTotalPrice()
        );
        saveNotification(event.getUserId(), event.getOrderId(), message);
    }

    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        String message = String.format(
                "Payment for order #%d was %s. Amount: %s",
                event.getOrderId(),
                event.getStatus(),
                event.getAmount()
        );
        saveNotification(event.getUserId(), event.getOrderId(), message);
    }

    private void saveNotification(Long userId, Long orderId, String message) {
        Notification notification = Notification.builder()
                .userId(userId)
                .orderId(orderId)
                .message(message)
                .status(NotificationStatus.SENT)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
        log.info("Notification sent to user id: {} - {}", userId, message);
    }

    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        return NotificationMapper.toResponse(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }
}
