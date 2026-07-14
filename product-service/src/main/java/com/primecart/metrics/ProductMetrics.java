package com.primecart.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ProductMetrics {

    private final Counter productCreatedCounter;
    private final Counter productUpdatedCounter;
    private final Counter productViewCounter;
    private final Counter activeProductsViewCounter;
    private final Counter productNotFoundCounter;
    private final Counter productDeletedCounter;

    public ProductMetrics(MeterRegistry meterRegistry) {

        this.productCreatedCounter = Counter.builder("primecart.product.created")
                                            .description("Total products created")
                                            .tag("service", "product-service")
                                            .register(meterRegistry);

        this.productUpdatedCounter = Counter.builder("primecart.product.updated")
                                            .description("Total products updated")
                                            .tag("service", "product-service")
                                            .register(meterRegistry);

        this.productViewCounter = Counter.builder("primecart.product.viewed")
                                         .description("Total product views")
                                         .tag("service", "product-service")
                                         .register(meterRegistry);

        this.activeProductsViewCounter = Counter.builder("primecart.product.active.viewed")
                                                .description("Total successful active product list views")
                                                .tag("service", "product-service")
                                                .register(meterRegistry);

        this.productNotFoundCounter = Counter.builder("primecart.product.notfound")
                                             .description("Total product not found requests")
                                             .tag("service", "product-service")
                                             .register(meterRegistry);

        this.productDeletedCounter = Counter.builder("primecart.product.deleted")
                                            .description("Total products deleted")
                                            .tag("service", "product-service")
                                            .register(meterRegistry);
    }

    public void incrementProductCreated() {
        productCreatedCounter.increment();
    }

    public void incrementProductUpdated(Long productId) {
        productUpdatedCounter.increment();
    }

    public void incrementProductView(Long productId) {
        productViewCounter.increment();
    }

    public void incrementActiveProductsView() {
        activeProductsViewCounter.increment();
    }

    public void incrementProductNotFound() {
        productNotFoundCounter.increment();
    }

    public void incrementProductDeleted(Long productId) {
        productDeletedCounter.increment();
    }
}
