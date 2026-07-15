package com.primecart.messaging.events;

public record ReservedInventoryItemEvent(Long productId, Integer quantity) {
}