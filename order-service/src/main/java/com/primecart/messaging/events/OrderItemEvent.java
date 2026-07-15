package com.primecart.messaging.events;

import java.math.BigDecimal;

public record OrderItemEvent(Long productId, Integer quantity, BigDecimal price) {
}