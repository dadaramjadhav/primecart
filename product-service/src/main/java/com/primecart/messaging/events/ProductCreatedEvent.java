package com.primecart.messaging.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductCreatedEvent(UUID eventId, String eventType, Long productId, String sku, String productName, BigDecimal price,
                                  Integer initialStock, Instant occurredAt) {
}