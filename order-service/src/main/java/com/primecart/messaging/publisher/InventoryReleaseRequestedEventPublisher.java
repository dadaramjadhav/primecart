package com.primecart.messaging.publisher;

import com.primecart.messaging.RabbitMqConstants;
import com.primecart.messaging.events.InventoryReleaseRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryReleaseRequestedEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(InventoryReleaseRequestedEvent event) {

        CorrelationData correlationData = new CorrelationData(event
                .eventId()
                .toString());

        log.info("Publishing InventoryReleaseRequestedEvent. eventId={}, orderId={}", event.eventId(), event.orderId());

        rabbitTemplate.convertAndSend(RabbitMqConstants.PRIME_CART_EXCHANGE, RabbitMqConstants.INVENTORY_RELEASE_REQUESTED_ROUTING_KEY,
                event, message -> {

                    message
                            .getMessageProperties()
                            .setHeader("eventId", event
                                    .eventId()
                                    .toString());

                    message
                            .getMessageProperties()
                            .setHeader("eventType", event.eventType());

                    message
                            .getMessageProperties()
                            .setHeader("eventVersion", event.eventVersion());

                    return message;
                }, correlationData);

        log.info("InventoryReleaseRequestedEvent submitted. eventId={}, orderId={}", event.eventId(), event.orderId());
    }
}