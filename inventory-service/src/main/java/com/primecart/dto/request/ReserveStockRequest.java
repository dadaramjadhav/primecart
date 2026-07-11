package com.primecart.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReserveStockRequest {

    private Long productId;

    private Integer quantity;

}