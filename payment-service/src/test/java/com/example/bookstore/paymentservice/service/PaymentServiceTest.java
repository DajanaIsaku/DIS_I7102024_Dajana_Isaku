package com.example.bookstore.paymentservice.service;

import com.example.bookstore.paymentservice.entity.PaymentStatus;
import com.example.bookstore.paymentservice.event.OrderCreatedEvent;
import com.example.bookstore.paymentservice.event.PaymentCompletedEvent;
import com.example.bookstore.paymentservice.messaging.PaymentEventPublisher;
import com.example.bookstore.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentEventPublisher paymentEventPublisher;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void processPayment_shouldCreatePaymentAndPublishEvent() {
        when(paymentRepository.findFirstByOrderId(2L)).thenReturn(Optional.empty());
        when(paymentRepository.save(any())).thenAnswer(invocation -> {
            com.example.bookstore.paymentservice.entity.Payment payment = invocation.getArgument(0);
            payment.setId(10L);
            return payment;
        });

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(2L)
                .userId(2L)
                .totalPrice(new BigDecimal("20.00"))
                .createdAt(LocalDateTime.now())
                .build();

        paymentService.processPayment(event);

        ArgumentCaptor<PaymentCompletedEvent> captor = ArgumentCaptor.forClass(PaymentCompletedEvent.class);
        verify(paymentEventPublisher).publishPaymentCompleted(captor.capture());
        assertThat(captor.getValue().getOrderId()).isEqualTo(2L);
        assertThat(captor.getValue().getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    void processPayment_duplicateOrder_shouldSkip() {
        when(paymentRepository.findFirstByOrderId(1L))
                .thenReturn(Optional.of(com.example.bookstore.paymentservice.entity.Payment.builder().id(5L).build()));

        paymentService.processPayment(OrderCreatedEvent.builder().orderId(1L).build());

        verify(paymentRepository, never()).save(any());
    }
}
