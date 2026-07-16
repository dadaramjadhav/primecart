package com.primecart.messaging.consumer;

import com.primecart.messaging.RabbitMqConstants;
import com.primecart.messaging.events.InventoryReservedEvent;
import com.primecart.service.OrderSagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryReservedEventConsumer {

    private final OrderSagaService orderSagaService;

    @RabbitListener(queues = RabbitMqConstants.ORDER_INVENTORY_RESERVED_QUEUE)
    public void consume(InventoryReservedEvent event) {

        log.info("Received InventoryReservedEvent. eventId={}, orderId={}, reservationId={}", event.eventId(), event.orderId(),
                 event.reservationId());

        validate(event);

        orderSagaService.handleInventoryReserved(event);

        log.info("InventoryReservedEvent processing completed. eventId={}, orderId={}", event.eventId(), event.orderId());
    }

    private void validate(InventoryReservedEvent event) {

        if (event == null) {
            throw new IllegalArgumentException("InventoryReservedEvent is required");
        }

        if (event.eventId() == null) {
            throw new IllegalArgumentException("eventId is required");
        }

        if (!"INVENTORY_RESERVED".equals(event.eventType())) {

            throw new IllegalArgumentException("Unsupported event type: " + event.eventType());
        }

        if (!Integer
                .valueOf(1)
                .equals(event.eventVersion())) {

            throw new IllegalArgumentException("Unsupported event version: " + event.eventVersion());
        }

        if (event.orderId() == null) {
            throw new IllegalArgumentException("orderId is required");
        }

        if (event.reservationId() == null) {
            throw new IllegalArgumentException("reservationId is required");
        }
    }
}