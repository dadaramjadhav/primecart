package com.primecart.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {

    private Long id;

    private String orderNumber;

    private String customerId;

    private String status;

    private BigDecimal totalAmount;

    private List<OrderItemResponse> items;

    private LocalDateTime createdAt;
}