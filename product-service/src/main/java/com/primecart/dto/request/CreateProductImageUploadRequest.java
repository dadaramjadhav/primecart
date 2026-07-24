package com.primecart.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateProductImageUploadRequest(

        @NotBlank(message = "File name is required") String fileName,

        @NotBlank(message = "Content type is required") String contentType,

        @NotNull(message = "File size is required") @Positive(message = "File size must be positive") Long fileSize) {
}