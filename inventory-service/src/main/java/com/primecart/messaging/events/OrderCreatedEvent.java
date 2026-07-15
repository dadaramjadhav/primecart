package com.primecart.messaging.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(UUID eventId, String eventType, Integer eventVersion, Long orderId, String orderNumber, String customerId,
                                BigDecimal totalAmount, List<OrderItemEvent> items, Instant occurredAt) {
}