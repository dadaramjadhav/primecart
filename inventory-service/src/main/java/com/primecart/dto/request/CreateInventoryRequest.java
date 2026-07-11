package com.primecart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateInventoryRequest {

    @NotNull
    private Long productId;

    @Min(0)
    private Integer availableQuantity;
}