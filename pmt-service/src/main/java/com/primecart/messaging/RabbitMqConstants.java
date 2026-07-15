package com.primecart.messaging;

public final class RabbitMqConstants {

    public static final String PRIME_CART_EXCHANGE = "primecart.exchange";
    public static final String PAYMENT_REQUESTED_ROUTING_KEY = "payment.requested";
    public static final String PAYMENT_REQUESTED_QUEUE = "payment.requested.queue";
    public static final String PAYMENT_COMPLETED_ROUTING_KEY = "payment.completed";
    public static final String PAYMENT_FAILED_ROUTING_KEY = "payment.failed";

    private RabbitMqConstants() {
    }
}