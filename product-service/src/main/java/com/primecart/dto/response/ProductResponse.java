package com.primecart.dto.response;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(

        Long id,

        String sku,

        Integer stock,

        String name,

        String description,

        BigDecimal price,

        String imageUrl,

        Long categoryId,

        String categoryName,

        Long brandId,

        String brandName,

        Boolean active,

        LocalDateTime createdAt,

        LocalDateTime updatedAt) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
