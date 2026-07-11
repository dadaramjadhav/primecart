package com.primecart.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(

        Long id,

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

        LocalDateTime updatedAt
) {
}