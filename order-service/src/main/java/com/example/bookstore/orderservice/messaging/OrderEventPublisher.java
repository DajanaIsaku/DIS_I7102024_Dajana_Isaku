package com.example.bookstore.orderservice.messaging;

import com.example.bookstore.orderservice.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${bookstore.rabbitmq.exchange}")
    private String exchange;

    @Value("${bookstore.rabbitmq.order-created-routing-key}")
    private String orderCreatedRoutingKey;

    public void publishOrderCreated(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(exchange, orderCreatedRoutingKey, event);
        log.info("Published OrderCreatedEvent for order id: {}", event.getOrderId());
    }
}
