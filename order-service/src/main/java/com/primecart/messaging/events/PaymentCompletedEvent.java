package com.primecart.messaging.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentCompletedEvent(UUID eventId, String eventType, Integer eventVersion, Long orderId, Long paymentId,
                                    String paymentNumber, BigDecimal amount, Instant occurredAt) {
}