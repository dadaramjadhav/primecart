package com.primecart.messaging.consumer;

import com.primecart.messaging.RabbitMqConstants;
import com.primecart.messaging.events.OrderCreatedEvent;
import com.primecart.messaging.events.OrderItemEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedEventConsumer {

    private final InventoryReservationService inventoryReservationService;

    @RabbitListener(queues = RabbitMqConstants.INVENTORY_ORDER_CREATED_QUEUE)
    public void consume(OrderCreatedEvent event) {

        log.info("Received OrderCreatedEvent. eventId={}, orderId={}, itemCount={}", event.eventId(), event.orderId(), event.items() != null ? event
                                                                                                                                               .items()
                                                                                                                                               .size() : 0);

        validate(event);

        inventoryReservationService.reserveInventory(event);

        log.info("OrderCreatedEvent processed. eventId={}, orderId={}", event.eventId(), event.orderId());
    }

    private void validate(OrderCreatedEvent event) {

        if (event == null) {
            throw new IllegalArgumentException("OrderCreatedEvent is required");
        }

        if (event.eventId() == null) {
            throw new IllegalArgumentException("eventId is required");
        }

        if (!Integer
                .valueOf(1)
                .equals(event.eventVersion())) {

            throw new IllegalArgumentException("Unsupported event version: " + event.eventVersion());
        }

        if (event.orderId() == null) {
            throw new IllegalArgumentException("orderId is required");
        }

        if (event.items() == null || event
                .items()
                .isEmpty()) {

            throw new IllegalArgumentException("Order items are required");
        }

        for (OrderItemEvent item : event.items()) {

            if (item.productId() == null) {
                throw new IllegalArgumentException("productId is required");
            }

            if (item.quantity() == null || item.quantity() <= 0) {

                throw new IllegalArgumentException("Quantity must be greater than zero");
            }
        }
    }
}