package com.primecart.messaging.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentRequestedEvent(UUID eventId, String eventType, Integer eventVersion, Long orderId, String customerId,
                                    BigDecimal amount, String paymentMethod, Instant occurredAt) {
}

