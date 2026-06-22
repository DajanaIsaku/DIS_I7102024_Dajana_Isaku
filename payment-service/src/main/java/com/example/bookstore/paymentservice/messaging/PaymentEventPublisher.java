package com.example.bookstore.paymentservice.messaging;

import com.example.bookstore.paymentservice.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${bookstore.rabbitmq.exchange}")
    private String exchange;

    @Value("${bookstore.rabbitmq.payment-completed-routing-key}")
    private String paymentCompletedRoutingKey;

    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        rabbitTemplate.convertAndSend(exchange, paymentCompletedRoutingKey, event);
        log.info("Published PaymentCompletedEvent for payment id: {}", event.getPaymentId());
    }
}
