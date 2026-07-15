package com.primecart.messaging.config;

import com.primecart.metrics.RabbitPublisherMetrics;
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
    private final RabbitPublisherMetrics rabbitPublisherMetrics;

    @PostConstruct
    public void configureCallbacks() {

        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {

            if (ack) {
                rabbitPublisherMetrics.incrementProductEventsPublished();
                log.debug("RabbitMQ confirmed message. correlationData={}", correlationData);
            } else {
                rabbitPublisherMetrics.incrementProductEventPublishFailures();
                log.error("RabbitMQ rejected message. " + "correlationData={}, cause={}", correlationData, cause);
            }
        });

        rabbitTemplate.setReturnsCallback(returned -> {
            rabbitPublisherMetrics.incrementProductEventPublishFailures();
            log.error("RabbitMQ returned unroutable message. " + "exchange={}, routingKey={}, replyText={}", returned.getExchange(),
                    returned.getRoutingKey(), returned.getReplyText());
        });
    }
}