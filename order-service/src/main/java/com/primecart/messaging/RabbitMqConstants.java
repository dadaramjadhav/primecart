package com.primecart.messaging;

public class RabbitMqConstants {
    public static final String PAYMENT_REQUESTED_ROUTING_KEY = "payment.requested";

    public static final String INVENTORY_ORDER_CREATED_QUEUE = "inventory.order-created.queue";

    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";

    public static final String PRIME_CART_EXCHANGE = "primecart.exchange";

    public static final String INVENTORY_RESERVED_ROUTING_KEY = "inventory.reserved";

    public static final String INVENTORY_RESERVATION_FAILED_ROUTING_KEY = "inventory.reservation-failed";

    public static final String PAYMENT_COMPLETED_ROUTING_KEY = "payment.completed";

    public static final String PAYMENT_FAILED_ROUTING_KEY = "payment.failed";

    public static final String INVENTORY_RELEASE_REQUESTED_ROUTING_KEY = "inventory.release-requested";

    public static final String ORDER_INVENTORY_RESERVED_QUEUE = "order.inventory-reserved.queue";

    public static final String ORDER_INVENTORY_RESERVATION_FAILED_QUEUE = "order.inventory-reservation-failed.queue";
}
