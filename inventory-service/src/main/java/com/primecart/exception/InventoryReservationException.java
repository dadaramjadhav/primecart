package com.primecart.exception;

import lombok.Getter;

@Getter
public class InventoryReservationException extends RuntimeException {

    private final Long orderId;
    private final Long productId;

    public InventoryReservationException(Long orderId, Long productId, String message) {

        super(message);

        this.orderId = orderId;
        this.productId = productId;
    }
}