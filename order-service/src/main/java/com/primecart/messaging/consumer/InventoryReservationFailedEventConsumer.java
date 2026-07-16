package com.primecart.messaging.consumer;

import com.primecart.messaging.RabbitMqConstants;
import com.primecart.messaging.events.InventoryReservationFailedEvent;
import com.primecart.service.OrderSagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryReservationFailedEventConsumer {

    private final OrderSagaService orderSagaService;

    @RabbitListener(queues = RabbitMqConstants.ORDER_INVENTORY_RESERVATION_FAILED_QUEUE)
    public void consume(InventoryReservationFailedEvent event) {

        log.info("Received InventoryReservationFailedEvent. " + "eventId={}, orderId={}, reason={}", event.eventId(), event.orderId(),
                 event.reason());

        validate(event);

        orderSagaService.handleInventoryReservationFailed(event);

        log.info("InventoryReservationFailedEvent processing completed. " + "eventId={}, orderId={}", event.eventId(), event.orderId());
    }

    private void validate(InventoryReservationFailedEvent event) {

        if (event == null) {
            throw new IllegalArgumentException("InventoryReservationFailedEvent is required");
        }

        if (event.eventId() == null) {
            throw new IllegalArgumentException("eventId is required");
        }

        if (!"INVENTORY_RESERVATION_FAILED".equals(event.eventType())) {

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

        if (event.reason() == null || event
                .reason()
                .isBlank()) {

            throw new IllegalArgumentException("Failure reason is required");
        }
    }
}