package com.primecart.messaging.config;

import com.primecart.messaging.RabbitMqConstants;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqProducerConfig {

    @Bean
    public TopicExchange primeCartExchange() {

        return new TopicExchange(
                RabbitMqConstants.PRIME_CART_EXCHANGE,
                true,
                false
        );
    }
}