package com.primecart.dto.response;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public record BrandResponse(Long id, String name, String description, String logoUrl, Boolean active, LocalDateTime createdAt,
                            LocalDateTime updatedAt) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}