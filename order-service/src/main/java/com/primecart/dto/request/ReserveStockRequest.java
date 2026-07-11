package com.primecart.dto.request;

import lombok.Data;

@Data
public class ReserveStockRequest {

    private Long productId;

    private Integer quantity;
}