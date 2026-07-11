package com.primecart.mapper;

import com.primecart.dto.response.OrderItemResponse;
import com.primecart.dto.response.OrderResponse;
import com.primecart.entity.Order;
import com.primecart.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {

        if (order == null) {
            return null;
        }

        return OrderResponse.builder()
                            .id(order.getId())
                            .orderNumber(order.getOrderNumber())
                            .customerId(order.getCustomerId())
                            .status(order.getStatus().toString())
                            .totalAmount(order.getTotalAmount())
                            .items(toOrderItemResponseList(order.getItems()))
                            .createdAt(order.getCreatedAt())
                            .updatedAt(order.getUpdatedAt())
                            .build();
    }

    public List<OrderResponse> toResponseList(List<Order> orders) {

        if (orders == null || orders.isEmpty()) {
            return Collections.emptyList();
        }

        return orders.stream()
                     .map(this::toResponse)
                     .toList();
    }

    private List<OrderItemResponse> toOrderItemResponseList(List<OrderItem> items) {

        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }

        return items.stream()
                    .map(this::toOrderItemResponse)
                    .toList();
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item) {

        return OrderItemResponse.builder()
                                .id(item.getId())
                                .productId(item.getProductId())
                                .productName(item.getProductName())
                                .price(item.getPrice())
                                .quantity(item.getQuantity())
                                .subtotal(item.getSubtotal())
                                .build();
    }
}