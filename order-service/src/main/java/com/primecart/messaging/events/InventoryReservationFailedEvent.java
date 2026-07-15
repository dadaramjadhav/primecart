package com.primecart.messaging.events;

import java.time.Instant;
import java.util.UUID;

public record InventoryReservationFailedEvent(UUID eventId, String eventType, Integer eventVersion, Long orderId, Long failedProductId, String reason,
                                              Instant occurredAt) {
}