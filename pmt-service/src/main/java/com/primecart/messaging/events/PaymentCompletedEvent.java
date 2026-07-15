package com.primecart.messaging.events;

import java.time.Instant;
import java.util.UUID;

public record PaymentCompletedEvent(UUID eventId, String eventType, Integer eventVersion, Long orderId, Long paymentId, String paymentNumber,
                                    Instant occurredAt) {
}