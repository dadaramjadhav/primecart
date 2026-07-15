package com.primecart.messaging;

public final class RabbitMqConstants {

    public static final String PRIME_CART_EVENTS_EXCHANGE = "primecart.events";
    public static final String PRIME_CART_SAGA_EXCHANGE = "primecart.exchange";
    public static final String PRODUCT_CREATED_ROUTING_KEY = "product.created";
    public static final String INVENTORY_PRODUCT_CREATED_QUEUE = "inventory.product-created.queue";
    public static final String INVENTORY_PRODUCT_CREATED_DLQ = "inventory.product-created.dlq";
    public static final String DEAD_LETTER_EXCHANGE = "primecart.dead-letter";

    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";

    public static final String INVENTORY_ORDER_CREATED_QUEUE = "inventory.order-created.queue";

    public static final String INVENTORY_RESERVED_ROUTING_KEY = "inventory.reserved";

    public static final String INVENTORY_RESERVATION_FAILED_ROUTING_KEY = "inventory.reservation-failed";

    public static final String INVENTORY_RELEASE_REQUESTED_ROUTING_KEY = "inventory.release-requested";

    private RabbitMqConstants() {
    }
}