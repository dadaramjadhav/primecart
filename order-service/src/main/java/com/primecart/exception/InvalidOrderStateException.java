package com.primecart.exception;

import com.primecart.entity.OrderStatus;
import lombok.Getter;

@Getter
public class InvalidOrderStateException extends RuntimeException {

    private final Long orderId;
    private final OrderStatus currentStatus;
    private final String operation;

    public InvalidOrderStateException(Long orderId, OrderStatus currentStatus, String operation, String message) {
        super(message);
        this.orderId = orderId;
        this.currentStatus = currentStatus;
        this.operation = operation;
    }
}