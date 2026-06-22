package com.example.bookstore.notificationservice.service;

import com.example.bookstore.notificationservice.entity.NotificationStatus;
import com.example.bookstore.notificationservice.event.OrderCreatedEvent;
import com.example.bookstore.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void handleOrderCreated_shouldPersistNotification() {
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(1L)
                .userId(2L)
                .totalPrice(new BigDecimal("25.00"))
                .createdAt(LocalDateTime.now())
                .build();

        notificationService.handleOrderCreated(event);

        ArgumentCaptor<com.example.bookstore.notificationservice.entity.Notification> captor =
                ArgumentCaptor.forClass(com.example.bookstore.notificationservice.entity.Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(2L);
        assertThat(captor.getValue().getStatus()).isEqualTo(NotificationStatus.SENT);
    }
}
