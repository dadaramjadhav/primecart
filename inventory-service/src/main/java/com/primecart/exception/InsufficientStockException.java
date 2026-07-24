package com.primecart.exception;

import lombok.Getter;

@Getter
public class InsufficientStockException extends RuntimeException {

    private final Long productId;
    private final int availableQuantity;
    private final int requestedQuantity;

    public InsufficientStockException(Long productId, int availableQuantity, int requestedQuantity) {
        super("Insufficient stock for product " + productId + ". Available: " + availableQuantity + ", requested: " + requestedQuantity);

        this.productId = productId;
        this.availableQuantity = availableQuantity;
        this.requestedQuantity = requestedQuantity;
    }
}