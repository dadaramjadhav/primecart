package com.primecart.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.primecart.messaging.RabbitMqConstants;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqProducerConfig {

    @Bean
    public TopicExchange primeCartExchange() {

        return new TopicExchange(RabbitMqConstants.PRIME_CART_EXCHANGE, true, false);
    }

    @Bean
    public MessageConverter rabbitMessageConverter(ObjectMapper objectMapper) {

        return new Jackson2JsonMessageConverter(objectMapper);
    }
}