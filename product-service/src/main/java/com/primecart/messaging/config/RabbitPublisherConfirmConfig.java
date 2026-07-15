package com.primecart.messaging.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RabbitPublisherConfirmConfig {

    private final RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void configureCallbacks() {

        rabbitTemplate.setConfirmCallback(
                (correlationData, ack, cause) -> {

                    if (ack) {
                        log.debug(
                                "RabbitMQ confirmed message. correlationData={}",
                                correlationData
                        );
                    } else {
                        log.error(
                                "RabbitMQ rejected message. " +
                                        "correlationData={}, cause={}",
                                correlationData,
                                cause
                        );
                    }
                }
        );

        rabbitTemplate.setReturnsCallback(
                returned -> log.error(
                        "RabbitMQ returned unroutable message. " +
                                "exchange={}, routingKey={}, replyText={}",
                        returned.getExchange(),
                        returned.getRoutingKey(),
                        returned.getReplyText()
                )
        );
    }
}