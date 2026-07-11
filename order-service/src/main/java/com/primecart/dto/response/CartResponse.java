package com.primecart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private Long cartId;

    private String userId;

    private List<CartItemResponse> items;

    private BigDecimal totalAmount;

    private Integer totalItems;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}