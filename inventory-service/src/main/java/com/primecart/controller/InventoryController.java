package com.primecart.controller;

import com.primecart.dto.request.CreateInventoryRequest;
import com.primecart.dto.request.ReserveStockRequest;
import com.primecart.dto.request.UpdateStockRequest;
import com.primecart.dto.response.InventoryResponse;
import com.primecart.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable Long productId) {

        log.info("GET /api/inventory/{} - Get inventory request received", productId);

        return ResponseEntity.ok(inventoryService.getInventory(productId));
    }

    @PutMapping("/{productId}/increase")
    public ResponseEntity<InventoryResponse> increaseStock(@PathVariable Long productId, @RequestBody UpdateStockRequest request) {

        log.info("PUT /api/inventory/{}/increase - Increase stock request received", productId);

        return ResponseEntity.ok(inventoryService.increaseStock(productId, request));
    }

    @PutMapping("/{productId}/decrease")
    public ResponseEntity<InventoryResponse> decreaseStock(@PathVariable Long productId, @RequestBody UpdateStockRequest request) {

        log.info("PUT /api/inventory/{}/decrease - Decrease stock request received", productId);

        return ResponseEntity.ok(inventoryService.decreaseStock(productId, request));
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(@RequestBody CreateInventoryRequest request) {

        log.info("POST /api/inventory - Create inventory request received");

        return ResponseEntity.ok(inventoryService.createInventory(request));
    }

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveStock(@RequestBody ReserveStockRequest request) {

        log.info("POST /api/inventory/reserve - Reserve stock request received");

        inventoryService.reserveStock(request);

        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/release")
    public ResponseEntity<Void> releaseStock(@RequestBody ReserveStockRequest request) {

        log.info("POST /api/inventory/release - Release stock request received");

        inventoryService.releaseStock(request);

        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmStock(@RequestBody ReserveStockRequest request) {

        log.info("POST /api/inventory/confirm - Confirm stock request received");

        inventoryService.confirmStock(request);

        return ResponseEntity
                .ok()
                .build();
    }
}