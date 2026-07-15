package com.primecart.messaging.config;

import com.primecart.messaging.RabbitMqConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConsumerConfig {

    @Bean
    public TopicExchange primeCartExchange() {

        return new TopicExchange(RabbitMqConstants.PRIME_CART_EXCHANGE, true, false
        );
    }

    @Bean
    public DirectExchange deadLetterExchange() {

        return new DirectExchange(
                RabbitMqConstants.DEAD_LETTER_EXCHANGE,
                true,
                false
        );
    }

    @Bean
    public Queue inventoryProductCreatedQueue() {

        return QueueBuilder
                .durable(
                        RabbitMqConstants
                                .INVENTORY_PRODUCT_CREATED_QUEUE
                )
                .deadLetterExchange(
                        RabbitMqConstants
                                .DEAD_LETTER_EXCHANGE
                )
                .deadLetterRoutingKey(
                        RabbitMqConstants
                                .INVENTORY_PRODUCT_CREATED_DLQ
                )
                .build();
    }

    @Bean
    public Queue inventoryProductCreatedDlq() {

        return QueueBuilder
                .durable(
                        RabbitMqConstants
                                .INVENTORY_PRODUCT_CREATED_DLQ
                )
                .build();
    }

    @Bean
    public Binding productCreatedBinding(
            Queue inventoryProductCreatedQueue,
            TopicExchange primeCartExchange) {

        return BindingBuilder
                .bind(inventoryProductCreatedQueue)
                .to(primeCartExchange)
                .with(
                        RabbitMqConstants
                                .PRODUCT_CREATED_ROUTING_KEY
                );
    }

    @Bean
    public Binding productCreatedDlqBinding(
            Queue inventoryProductCreatedDlq,
            DirectExchange deadLetterExchange) {

        return BindingBuilder
                .bind(inventoryProductCreatedDlq)
                .to(deadLetterExchange)
                .with(
                        RabbitMqConstants
                                .INVENTORY_PRODUCT_CREATED_DLQ
                );
    }
}