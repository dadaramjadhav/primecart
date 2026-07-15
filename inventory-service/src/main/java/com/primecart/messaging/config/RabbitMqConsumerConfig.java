package com.primecart.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.primecart.messaging.RabbitMqConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConsumerConfig {

    @Bean
    public TopicExchange primeCartEventsExchange() {
        return new TopicExchange(RabbitMqConstants.PRIME_CART_EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange primeCartSagaExchange() {

        return new TopicExchange(RabbitMqConstants.PRIME_CART_SAGA_EXCHANGE, true, false);
    }

    @Bean
    public Queue inventoryOrderCreatedQueue() {

        return QueueBuilder
                .durable(RabbitMqConstants.INVENTORY_ORDER_CREATED_QUEUE)
                .build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {

        return new DirectExchange(RabbitMqConstants.DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    public Queue inventoryProductCreatedQueue() {

        return QueueBuilder
                .durable(RabbitMqConstants.INVENTORY_PRODUCT_CREATED_QUEUE)
                .deadLetterExchange(RabbitMqConstants.DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(RabbitMqConstants.INVENTORY_PRODUCT_CREATED_DLQ)
                .build();
    }

    @Bean
    public Queue inventoryProductCreatedDlq() {

        return QueueBuilder
                .durable(RabbitMqConstants.INVENTORY_PRODUCT_CREATED_DLQ)
                .build();
    }

    @Bean
    public Binding inventoryOrderCreatedBinding(Queue inventoryOrderCreatedQueue, @Qualifier("primeCartSagaExchange") TopicExchange primeCartSagaExchange) {
 
        return BindingBuilder
                .bind(inventoryOrderCreatedQueue)
                .to(primeCartSagaExchange)
                .with(RabbitMqConstants.ORDER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding productCreatedBinding(Queue inventoryProductCreatedQueue, @Qualifier("primeCartEventsExchange") TopicExchange primeCartEventsExchange) {

        return BindingBuilder
                .bind(inventoryProductCreatedQueue)
                .to(primeCartEventsExchange)
                .with(RabbitMqConstants.PRODUCT_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding productCreatedDlqBinding(Queue inventoryProductCreatedDlq, DirectExchange deadLetterExchange) {

        return BindingBuilder
                .bind(inventoryProductCreatedDlq)
                .to(deadLetterExchange)
                .with(RabbitMqConstants.INVENTORY_PRODUCT_CREATED_DLQ);
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