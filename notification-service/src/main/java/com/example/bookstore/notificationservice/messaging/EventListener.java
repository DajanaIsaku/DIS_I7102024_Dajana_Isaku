package com.example.bookstore.notificationservice.messaging;

import com.example.bookstore.notificationservice.event.OrderCreatedEvent;
import com.example.bookstore.notificationservice.event.PaymentCompletedEvent;
import com.example.bookstore.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${bookstore.rabbitmq.order-created-queue}")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Notification service received OrderCreatedEvent for order id: {}", event.getOrderId());
        notificationService.handleOrderCreated(event);
    }

    @RabbitListener(queues = "${bookstore.rabbitmq.payment-completed-queue}")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("Notification service received PaymentCompletedEvent for order id: {}", event.getOrderId());
        notificationService.handlePaymentCompleted(event);
    }
}
