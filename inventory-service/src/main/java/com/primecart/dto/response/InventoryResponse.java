package com.primecart.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InventoryResponse {

    private Long productId;

    private Integer availableQuantity;

    private Integer reservedQuantity;

}