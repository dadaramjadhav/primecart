package com.primecart.messaging;

public final class RabbitMqConstants {

    public static final String PRIME_CART_EXCHANGE =
            "primecart.events";
    public static final String PRODUCT_CREATED_ROUTING_KEY =
            "product.created";
    public static final String INVENTORY_PRODUCT_CREATED_QUEUE =
            "inventory.product-created.queue";
    public static final String INVENTORY_PRODUCT_CREATED_DLQ =
            "inventory.product-created.dlq";
    public static final String DEAD_LETTER_EXCHANGE =
            "primecart.dead-letter";

    private RabbitMqConstants() {
    }
}