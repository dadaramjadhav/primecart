package com.primecart.messaging.publisher;

import com.primecart.messaging.RabbitMqConstants;
import com.primecart.messaging.events.InventoryReservedEvent;
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
public class InventoryReservedEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(InventoryReservedEvent event) {

        CorrelationData correlationData = new CorrelationData(event
                .eventId()
                .toString());

        rabbitTemplate.convertAndSend(RabbitMqConstants.PRIME_CART_SAGA_EXCHANGE, RabbitMqConstants.INVENTORY_RESERVED_ROUTING_KEY, event,
                message -> {

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

        log.info("InventoryReservedEvent submitted. eventId={}, orderId={}", event.eventId(), event.orderId());
    }
}