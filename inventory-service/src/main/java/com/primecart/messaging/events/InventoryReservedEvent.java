package com.primecart.messaging.events;

import java.time.Instant;
import java.util.UUID;

public record InventoryReservedEvent(UUID eventId, String eventType, Integer eventVersion, Long orderId, UUID reservationId, Instant occurredAt) {
}