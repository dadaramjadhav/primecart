package com.primecart.dto.response;

import com.primecart.entity.Order;
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
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
    private LocalDateTime updatedAt;   // <-- Add this

    public static OrderResponse from(Order order) {

        return OrderResponse.builder()
                            .id(order.getId())
                            .orderNumber(order.getOrderNumber())
                            .customerId(order.getCustomerId())
                            .status(order.getStatus().name())
                            .totalAmount(order.getTotalAmount())
                            .createdAt(order.getCreatedAt())
                            .items(
                                    order.getItems()
                                         .stream()
                                         .map(OrderItemResponse::from)
                                         .toList()
                            )
                            .build();
    }
}