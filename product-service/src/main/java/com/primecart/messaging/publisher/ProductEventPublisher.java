package com.primecart.messaging.publisher;

import com.primecart.messaging.RabbitMqConstants;
import com.primecart.messaging.events.ProductCreatedEvent;
import com.primecart.metrics.RabbitPublisherMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitPublisherMetrics rabbitPublisherMetrics;

    public void publishProductCreated(ProductCreatedEvent event) {
        try {
            rabbitTemplate.convertAndSend(RabbitMqConstants.PRIME_CART_EXCHANGE, RabbitMqConstants.PRODUCT_CREATED_ROUTING_KEY, event);

//            rabbitPublisherMetrics.incrementProductEventsPublished();

            log.info("ProductCreatedEvent published. eventId={}, productId={}", event.eventId(), event.productId());
        } catch (Exception exception) {

            rabbitPublisherMetrics.incrementProductEventPublishFailures();

            log.error("Failed to publish ProductCreatedEvent. " + "eventId={}, productId={}", event.eventId(), event.productId(), exception);

            throw exception;
        }
//        log.info("Publishing ProductCreatedEvent. eventId={}, productId={}", event.eventId(), event.productId());
//
//        rabbitTemplate.convertAndSend(RabbitMqConstants.PRIME_CART_EXCHANGE, RabbitMqConstants.PRODUCT_CREATED_ROUTING_KEY, event);
    }
}