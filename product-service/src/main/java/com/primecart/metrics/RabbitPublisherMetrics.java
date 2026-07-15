package com.primecart.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class RabbitPublisherMetrics {

    private final Counter productEventsPublished;
    private final Counter productEventPublishFailures;

    public RabbitPublisherMetrics(MeterRegistry meterRegistry) {

        this.productEventsPublished = Counter
                .builder("primecart.rabbitmq.product.events.published")
                .description("Total ProductCreatedEvent messages published")
                .tag("service", "product-service")
                .tag("event_type", "product_created")
                .register(meterRegistry);

        this.productEventPublishFailures = Counter
                .builder("primecart.rabbitmq.product.events.publish.failed")
                .description("Total ProductCreatedEvent publishing failures")
                .tag("service", "product-service")
                .tag("event_type", "product_created")
                .register(meterRegistry);
    }

    public void incrementProductEventsPublished() {
        productEventsPublished.increment();
    }

    public void incrementProductEventPublishFailures() {
        productEventPublishFailures.increment();
    }
}