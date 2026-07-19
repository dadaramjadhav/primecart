package com.primecart.client;

import com.primecart.config.InventoryClientCredentialsFeignConfig;
import com.primecart.dto.request.ReserveStockRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service",
             url = "http://localhost:8084",
             configuration = InventoryClientCredentialsFeignConfig.class)
public interface InventoryClient {

    @PostMapping("/api/inventory/reserve")
    void reserveStock(
            @RequestBody
            ReserveStockRequest request);

    @PostMapping("/api/inventory/release")
    void releaseStock(
            @RequestBody
            ReserveStockRequest request);

    @PostMapping("/api/inventory/confirm")
    void confirmStock(
            @RequestBody
            ReserveStockRequest request);
}