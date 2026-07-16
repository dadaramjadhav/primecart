package com.primecart.service;

import com.primecart.client.InventoryClient;
import com.primecart.dto.request.ReserveStockRequest;
import com.primecart.exception.InventoryReservationException;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryIntegrationService {

    private static final String INVENTORY_SERVICE = "inventoryService";

    private final InventoryClient inventoryClient;

    /*
     * Reserve inventory while creating an order.
     */
    @Retry(name = INVENTORY_SERVICE,
           fallbackMethod = "reserveStockFallback")
    @CircuitBreaker(name = INVENTORY_SERVICE,
                    fallbackMethod = "reserveStockFallback")
    @Bulkhead(name = INVENTORY_SERVICE,
              type = Bulkhead.Type.SEMAPHORE,
              fallbackMethod = "reserveStockFallback")
    public void reserveStock(ReserveStockRequest request) {

        log.info("Calling Inventory Service to reserve stock. productId={}, quantity={}", request.getProductId(), request.getQuantity());

        inventoryClient.reserveStock(request);

        log.info("Inventory reserved successfully. productId={}, quantity={}", request.getProductId(), request.getQuantity());
    }

    /*
     * Release inventory when an order is cancelled or payment fails.
     */
    @Retry(name = INVENTORY_SERVICE,
           fallbackMethod = "releaseStockFallback")
    @CircuitBreaker(name = INVENTORY_SERVICE,
                    fallbackMethod = "releaseStockFallback")
    @Bulkhead(name = INVENTORY_SERVICE,
              type = Bulkhead.Type.SEMAPHORE,
              fallbackMethod = "releaseStockFallback")
    public void releaseStock(ReserveStockRequest request) {

        log.info("Calling Inventory Service to release stock. productId={}, quantity={}", request.getProductId(), request.getQuantity());

        inventoryClient.releaseStock(request);

        log.info("Inventory released successfully. productId={}, quantity={}", request.getProductId(), request.getQuantity());
    }

    /*
     * Confirm previously reserved inventory after successful payment.
     */
    @Retry(name = INVENTORY_SERVICE,
           fallbackMethod = "confirmStockFallback")
    @CircuitBreaker(name = INVENTORY_SERVICE,
                    fallbackMethod = "confirmStockFallback")
    @Bulkhead(name = INVENTORY_SERVICE,
              type = Bulkhead.Type.SEMAPHORE,
              fallbackMethod = "confirmStockFallback")
    public void confirmStock(ReserveStockRequest request) {

        log.info("Calling Inventory Service to confirm stock. productId={}, quantity={}", request.getProductId(), request.getQuantity());

        inventoryClient.confirmStock(request);

        log.info("Inventory confirmed successfully. productId={}, quantity={}", request.getProductId(), request.getQuantity());
    }

    private void reserveStockFallback(ReserveStockRequest request, Throwable throwable) {

        if (throwable instanceof BulkheadFullException) {

            log.warn("Inventory Bulkhead is full. Call rejected. productId={}", request.getProductId());

            throw new InventoryReservationException("Inventory Service is currently busy. Please try again.");
        }

        if (throwable instanceof CallNotPermittedException) {

            log.warn("Inventory Circuit Breaker is open. productId={}", request.getProductId());

            throw new InventoryReservationException("Inventory Service is temporarily unavailable.");
        }
        log.error("Inventory reservation failed. productId={}, quantity={}, reason={}", request.getProductId(), request.getQuantity(),
                  throwable.getMessage(), throwable);

        throw new InventoryReservationException(
                "Inventory Service is temporarily unavailable. " + "Unable to reserve inventory for product " + request.getProductId());
    }

    private void releaseStockFallback(ReserveStockRequest request, Throwable throwable) {

        log.error("Inventory release failed. productId={}, quantity={}, reason={}", request.getProductId(), request.getQuantity(),
                  throwable.getMessage(), throwable);

        throw new InventoryReservationException(
                "Inventory Service is temporarily unavailable. " + "Unable to release inventory for product " + request.getProductId());
    }

    private void confirmStockFallback(ReserveStockRequest request, Throwable throwable) {

        log.error("Inventory confirmation failed. productId={}, quantity={}, reason={}", request.getProductId(), request.getQuantity(),
                  throwable.getMessage(), throwable);

        throw new InventoryReservationException(
                "Inventory Service is temporarily unavailable. " + "Unable to confirm inventory for product " + request.getProductId());
    }
}