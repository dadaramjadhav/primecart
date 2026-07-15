package com.primecart.messaging.events;

import java.time.Instant;
import java.util.UUID;

public record PaymentFailedEvent(UUID eventId, String eventType, Integer eventVersion, Long orderId, String reason, Instant occurredAt) {
}