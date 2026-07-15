package com.primecart.messaging.consumer;

import com.primecart.entity.Inventory;
import com.primecart.exception.InventoryReservationException;
import com.primecart.messaging.entity.ProcessedEvent;
import com.primecart.messaging.events.InventoryReservedEvent;
import com.primecart.messaging.events.OrderCreatedEvent;
import com.primecart.messaging.events.OrderItemEvent;
import com.primecart.messaging.events.ReservedInventoryItemEvent;
import com.primecart.repository.InventoryRepository;
import com.primecart.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryReservationService {

    private final InventoryRepository inventoryRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void reserveInventory(OrderCreatedEvent event) {

        //-------------------------------------------------------------
        // Idempotency
        //-------------------------------------------------------------
        if (processedEventRepository.existsByEventId(event.eventId())) {

            log.info("OrderCreatedEvent already processed. eventId={}, orderId={}", event.eventId(), event.orderId());

            return;
        }

        //-------------------------------------------------------------
        // Reserve every product
        //-------------------------------------------------------------
        for (OrderItemEvent item : event.items()) {

            Inventory inventory = inventoryRepository
                    .findByProductIdForUpdate(item.productId())
                    .orElseThrow(() -> new InventoryReservationException(event.orderId(), item.productId(),
                            "Inventory not found for product: " + item.productId()));

            int availableQuantity = inventory.getAvailableQuantity();

            if (availableQuantity < item.quantity()) {

                throw new InventoryReservationException(event.orderId(), item.productId(),
                        "Insufficient stock. Available: " + availableQuantity + ", requested: " + item.quantity());
            }

            inventory.setAvailableQuantity(availableQuantity - item.quantity());

            inventory.setReservedQuantity(inventory.getReservedQuantity() + item.quantity());

            inventory.setUpdatedAt(LocalDateTime.now());
        }

        //-------------------------------------------------------------
        // Record processed event in same transaction
        //-------------------------------------------------------------
        ProcessedEvent processedEvent = ProcessedEvent
                .builder()
                .eventId(event.eventId())
                .eventType(event.eventType())
                .processedAt(LocalDateTime.now())
                .build();

        processedEventRepository.save(processedEvent);

        //-------------------------------------------------------------
        // Publish internal success event
        //-------------------------------------------------------------
        List<ReservedInventoryItemEvent> reservedItems = event
                .items()
                .stream()
                .map(item -> new ReservedInventoryItemEvent(item.productId(), item.quantity()))
                .toList();
        UUID reservationId = UUID.randomUUID();

        InventoryReservedEvent reservedEvent = new InventoryReservedEvent(UUID.randomUUID(), "INVENTORY_RESERVED", 1, event.orderId(),
                reservationId, Instant.now());

        applicationEventPublisher.publishEvent(reservedEvent);
        log.info("Inventory reserved. orderId={}, reservationId={}, sourceEventId={}", event.orderId(), reservationId, event.eventId());
    }
}