package com.example.bookstore.notificationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    TopicExchange bookstoreExchange(@Value("${bookstore.rabbitmq.exchange}") String exchange) {
        return new TopicExchange(exchange);
    }

    @Bean
    Queue notificationOrderCreatedQueue(@Value("${bookstore.rabbitmq.order-created-queue}") String queueName) {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    Queue notificationPaymentCompletedQueue(@Value("${bookstore.rabbitmq.payment-completed-queue}") String queueName) {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    Binding notificationOrderCreatedBinding(@Qualifier("notificationOrderCreatedQueue") Queue notificationOrderCreatedQueue,
                                            TopicExchange bookstoreExchange,
                                            @Value("${bookstore.rabbitmq.order-created-routing-key}") String routingKey) {
        return BindingBuilder.bind(notificationOrderCreatedQueue).to(bookstoreExchange).with(routingKey);
    }

    @Bean
    Binding notificationPaymentCompletedBinding(@Qualifier("notificationPaymentCompletedQueue") Queue notificationPaymentCompletedQueue,
                                                TopicExchange bookstoreExchange,
                                                @Value("${bookstore.rabbitmq.payment-completed-routing-key}") String routingKey) {
        return BindingBuilder.bind(notificationPaymentCompletedQueue).to(bookstoreExchange).with(routingKey);
    }

    @Bean
    Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
