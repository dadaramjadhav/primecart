package com.primecart.metrics;

import com.primecart.entity.Inventory;
import com.primecart.repository.InventoryRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.Tags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class InventoryStockMetrics {

    private final InventoryRepository inventoryRepository;
    private final MultiGauge availableQuantityGauge;

    public InventoryStockMetrics(InventoryRepository inventoryRepository, MeterRegistry meterRegistry) {

        this.inventoryRepository = inventoryRepository;

        this.availableQuantityGauge = MultiGauge
                .builder("primecart.inventory.available.quantity")
                .description("Current available inventory quantity by product")
                .register(meterRegistry);
    }

    @Transactional(readOnly = true)
    @Scheduled(fixedDelayString = "${primecart.metrics.inventory.refresh-interval-ms:30000}",
               initialDelayString = "${primecart.metrics.inventory.initial-delay-ms:5000}")
    public void refreshAvailableQuantities() {

        List<? extends MultiGauge.Row<?>> rows = inventoryRepository
                .findAll()
                .stream()
                .map(this::toGaugeRow)
                .toList();

        availableQuantityGauge.register(rows, true);

        log.debug("Refreshed inventory quantity metrics: productCount={}", rows.size());
    }

    private MultiGauge.Row<?> toGaugeRow(Inventory inventory) {
        Tags tags = Tags.of("product_id", inventory
                .getProductId()
                .toString(), "sku", inventory.getSku());

        return MultiGauge.Row.of(tags, inventory.getAvailableQuantity());
    }
}