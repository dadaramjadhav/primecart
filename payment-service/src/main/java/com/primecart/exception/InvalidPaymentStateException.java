package com.primecart.exception;

import com.primecart.entity.PaymentStatus;
import lombok.Getter;

@Getter
public class InvalidPaymentStateException extends RuntimeException {

    private final Long paymentId;
    private final PaymentStatus currentStatus;
    private final String operation;

    public InvalidPaymentStateException(Long paymentId, PaymentStatus currentStatus, String operation, String message) {
        super(message);
        this.paymentId = paymentId;
        this.currentStatus = currentStatus;
        this.operation = operation;
    }
}