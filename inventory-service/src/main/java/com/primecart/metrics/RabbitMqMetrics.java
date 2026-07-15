package com.primecart.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqMetrics {

    private final Counter productEventsConsumed;
    private final Counter duplicateProductEvents;
    private final Counter productEventFailures;

    public RabbitMqMetrics(MeterRegistry meterRegistry) {

        this.productEventsConsumed = Counter.builder("primecart.rabbitmq.product.events.consumed").description(
                "Total ProductCreatedEvent messages consumed successfully").tag("service", "inventory-service").tag("event_type",
                "product_created").register(meterRegistry);

        this.duplicateProductEvents = Counter.builder("primecart.rabbitmq.product.events.duplicate").description(
                "Total duplicate ProductCreatedEvent messages received").tag("service", "inventory-service").tag("event_type",
                "product_created").register(meterRegistry);

        this.productEventFailures = Counter.builder("primecart.rabbitmq.product.events.failed").description(
                "Total failed ProductCreatedEvent consumption attempts").tag("service", "inventory-service").tag("event_type",
                "product_created").register(meterRegistry);
    }

    public void incrementProductEventsConsumed() {
        productEventsConsumed.increment();
    }

    public void incrementDuplicateProductEvents() {
        duplicateProductEvents.increment();
    }

    public void incrementProductEventFailures() {
        productEventFailures.increment();
    }
}