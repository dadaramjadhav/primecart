package com.primecart.dto.response;

import com.primecart.entity.PaymentMethod;
import com.primecart.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;

    private String paymentNumber;

    private Long orderId;

    private String customerId;

    private BigDecimal amount;

    private PaymentMethod method;

    private PaymentStatus status;

    private String transactionId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}