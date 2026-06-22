package com.example.bookstore.paymentservice.service;

import com.example.bookstore.paymentservice.dto.PaymentResponse;
import com.example.bookstore.paymentservice.entity.Payment;
import com.example.bookstore.paymentservice.entity.PaymentStatus;
import com.example.bookstore.paymentservice.event.OrderCreatedEvent;
import com.example.bookstore.paymentservice.event.PaymentCompletedEvent;
import com.example.bookstore.paymentservice.exception.ResourceNotFoundException;
import com.example.bookstore.paymentservice.mapper.PaymentMapper;
import com.example.bookstore.paymentservice.messaging.PaymentEventPublisher;
import com.example.bookstore.paymentservice.repository.PaymentRepository;
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
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;

    public void processPayment(OrderCreatedEvent event) {
        if (paymentRepository.findFirstByOrderId(event.getOrderId()).isPresent()) {
            log.warn("Payment already exists for order id: {}", event.getOrderId());
            return;
        }

        // Simulate payment processing: even order IDs succeed, odd order IDs fail
        PaymentStatus status = event.getOrderId() % 2 == 0 ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;

        Payment payment = Payment.builder()
                .orderId(event.getOrderId())
                .amount(event.getTotalPrice())
                .status(status)
                .paymentDate(LocalDateTime.now())
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment processed with status {} for order id: {}", saved.getStatus(), saved.getOrderId());

        if (saved.getStatus() == PaymentStatus.COMPLETED) {
            paymentEventPublisher.publishPaymentCompleted(PaymentCompletedEvent.builder()
                    .paymentId(saved.getId())
                    .orderId(saved.getOrderId())
                    .userId(event.getUserId())
                    .amount(saved.getAmount())
                    .status(saved.getStatus())
                    .paymentDate(saved.getPaymentDate())
                    .build());
        }
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return PaymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .map(PaymentMapper::toResponse)
                .toList();
    }
}
