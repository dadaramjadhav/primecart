package com.primecart.messaging.consumer;

import com.primecart.entity.Inventory;
import com.primecart.messaging.RabbitMqConstants;
import com.primecart.messaging.events.ProductCreatedEvent;
import com.primecart.repository.InventoryRepository;
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

    @Transactional
    @RabbitListener(
            queues =
                    RabbitMqConstants
                            .INVENTORY_PRODUCT_CREATED_QUEUE
    )
    public void consume(ProductCreatedEvent event) {

        log.info("Received ProductCreatedEvent. eventId={}, productId={}", event.eventId(), event.productId());

        if (inventoryRepository
                .existsByProductId(event.productId())) {

            log.info("Inventory already exists for productId={}. " + "Ignoring duplicate eventId={}", event.productId(), event.eventId());

            return;
        }

        Inventory inventory =
                Inventory.builder()
                         .productId(event.productId())
                         .sku(event.sku())
                         .availableQuantity(
                                 event.initialStock() == null
                                         ? 0
                                         : event.initialStock()
                         )
                         .reservedQuantity(0)
                         .createdAt(LocalDateTime.now())
                         .updatedAt(LocalDateTime.now())
                         .build();

        inventoryRepository.save(inventory);

        log.info(
                "Inventory created asynchronously for productId={}",
                event.productId()
        );
    }
}