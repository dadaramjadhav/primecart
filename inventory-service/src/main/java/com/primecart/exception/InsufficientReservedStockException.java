package com.primecart.exception;

import lombok.Getter;

@Getter
public class InsufficientReservedStockException extends RuntimeException {

    private final Long productId;
    private final int reservedQuantity;
    private final int requestedQuantity;

    public InsufficientReservedStockException(Long productId, int reservedQuantity, int requestedQuantity) {
        super("Insufficient reserved stock for product " + productId + ". Reserved: " + reservedQuantity + ", requested: " + requestedQuantity);

        this.productId = productId;
        this.reservedQuantity = reservedQuantity;
        this.requestedQuantity = requestedQuantity;
    }
}