package com.example.bookstore.paymentservice.messaging;

import com.example.bookstore.paymentservice.event.OrderCreatedEvent;
import com.example.bookstore.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedListener {

    private final PaymentService paymentService;

    @RabbitListener(queues = "${bookstore.rabbitmq.order-created-queue}")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent for order id: {}", event.getOrderId());
        paymentService.processPayment(event);
    }
}
