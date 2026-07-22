package com.primecart.mapper;

import com.primecart.dto.response.PaymentResponse;
import com.primecart.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {

        if (payment == null) {
            return null;
        }

        return PaymentResponse.builder()
                              .id(payment.getId())
                              .paymentNumber(payment.getPaymentNumber())
                              .orderId(payment.getOrderId())
                              .customerId(payment.getCustomerId())
                              .amount(payment.getAmount())
                              .method(payment.getMethod())
                              .status(payment.getStatus())
                              .transactionId(payment.getTransactionId())
                              .createdAt(payment.getCreatedAt())
                              .updatedAt(payment.getUpdatedAt())
                              .build();
    }
}