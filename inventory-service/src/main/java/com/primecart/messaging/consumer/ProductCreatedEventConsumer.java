package com.primecart.messaging.consumer;

import com.primecart.entity.Inventory;
import com.primecart.messaging.RabbitMqConstants;
import com.primecart.messaging.entity.ProcessedEvent;
import com.primecart.messaging.events.ProductCreatedEvent;
import com.primecart.repository.InventoryRepository;
import com.primecart.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCreatedEventConsumer {

    private final InventoryRepository inventoryRepository;
    private final ProcessedEventRepository processedEventRepository;

    @Transactional
    @RabbitListener(queues = RabbitMqConstants.INVENTORY_PRODUCT_CREATED_QUEUE)
    public void consume(ProductCreatedEvent event) {

        log.info("Received ProductCreatedEvent. eventId={}, productId={}, eventType={}", event.eventId(), event.productId(),
                event.eventType());

        validate(event);

        if (processedEventRepository.existsByEventId(event.eventId())) {

            log.warn("Duplicate event ignored. eventId={}, productId={}, eventType={}", event.eventId(), event.productId(),
                    event.eventType());

            return;
        }

        if (inventoryRepository.existsByProductId(event.productId())) {

            log.warn("Inventory already exists for product. " + "Recording event as processed. eventId={}, productId={}", event.eventId(),
                    event.productId());

            saveProcessedEvent(event);
            return;
        }

        Inventory inventory = Inventory
                .builder()
                .productId(event.productId())
                .sku(event.sku())
                .availableQuantity(event.initialStock())
                .reservedQuantity(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        inventoryRepository.save(inventory);

        saveProcessedEvent(event);

        log.info("ProductCreatedEvent processed successfully. " + "eventId={}, productId={}, sku={}", event.eventId(), event.productId(),
                event.sku());
    }

    private void saveProcessedEvent(ProductCreatedEvent event) {

        ProcessedEvent processedEvent = ProcessedEvent
                .builder()
                .eventId(event.eventId())
                .eventType(event.eventType())
                .processedAt(LocalDateTime.now())
                .build();

        processedEventRepository.save(processedEvent);
    }

    private void validate(ProductCreatedEvent event) {

        if (event.eventId() == null) {
            throw new IllegalArgumentException("eventId is required");
        }

        if (event.eventType() == null || event
                .eventType()
                .isBlank()) {
            throw new IllegalArgumentException("eventType is required");
        }

        if (event.productId() == null) {
            throw new IllegalArgumentException("productId is required");
        }

        if (event.sku() == null || event
                .sku()
                .isBlank()) {
            throw new IllegalArgumentException("sku is required");
        }

        if (event.initialStock() == null || event.initialStock() < 0) {
            throw new IllegalArgumentException("initialStock must be zero or greater");
        }

//        if (!Integer
//                .valueOf(1)
//                .equals(event.eventVersion())) {
//            throw new IllegalArgumentException("Unsupported event version: " + event.eventVersion());
//        }
    }
}