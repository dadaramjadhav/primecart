package com.primecart.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.primecart.messaging.RabbitMqConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public TopicExchange primeCartExchange() {
        return new TopicExchange(RabbitMqConstants.PRIME_CART_EXCHANGE, true, false);
    }

    @Bean
    public Queue orderInventoryReservedQueue() {

        return QueueBuilder
                .durable(RabbitMqConstants.ORDER_INVENTORY_RESERVED_QUEUE)
                .build();
    }

    @Bean
    public Binding orderInventoryReservedBinding(Queue orderInventoryReservedQueue, TopicExchange primeCartExchange) {

        return BindingBuilder
                .bind(orderInventoryReservedQueue)
                .to(primeCartExchange)
                .with(RabbitMqConstants.INVENTORY_RESERVED_ROUTING_KEY);
    }

    @Bean
    public Queue orderInventoryReservationFailedQueue() {

        return QueueBuilder
                .durable(RabbitMqConstants.ORDER_INVENTORY_RESERVATION_FAILED_QUEUE)
                .build();
    }

    @Bean
    public Binding orderInventoryReservationFailedBinding(Queue orderInventoryReservationFailedQueue, TopicExchange primeCartExchange) {

        return BindingBuilder
                .bind(orderInventoryReservationFailedQueue)
                .to(primeCartExchange)
                .with(RabbitMqConstants.INVENTORY_RESERVATION_FAILED_ROUTING_KEY);
    }

    @Bean
    public Queue orderPaymentCompletedQueue() {

        return QueueBuilder
                .durable(RabbitMqConstants.ORDER_PAYMENT_COMPLETED_QUEUE)
                .build();
    }

    @Bean
    public Binding orderPaymentCompletedBinding(Queue orderPaymentCompletedQueue, TopicExchange primeCartExchange) {

        return BindingBuilder
                .bind(orderPaymentCompletedQueue)
                .to(primeCartExchange)
                .with(RabbitMqConstants.PAYMENT_COMPLETED_ROUTING_KEY);
    }

    @Bean
    public Queue orderPaymentFailedQueue() {

        return QueueBuilder
                .durable(RabbitMqConstants.ORDER_PAYMENT_FAILED_QUEUE)
                .build();
    }

    @Bean
    public Binding orderPaymentFailedBinding(Queue orderPaymentFailedQueue, TopicExchange primeCartExchange) {

        return BindingBuilder
                .bind(orderPaymentFailedQueue)
                .to(primeCartExchange)
                .with(RabbitMqConstants.PAYMENT_FAILED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter rabbitMessageConverter(ObjectMapper objectMapper) {

        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(RabbitTemplateConfigurer configurer, ConnectionFactory connectionFactory) {

        RabbitTemplate rabbitTemplate = new RabbitTemplate();

        configurer.configure(rabbitTemplate, connectionFactory);

        return rabbitTemplate;
    }
}