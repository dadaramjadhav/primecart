package com.primecart.controller;

import com.primecart.dto.request.CreateProductImageUploadRequest;
import com.primecart.dto.response.ProductImageUploadResponse;
import com.primecart.service.ProductImageStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product-images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageStorageService productImageStorageService;

    @PostMapping("/upload-url")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductImageUploadResponse> createUploadUrl(
            @Valid
            @RequestBody
            CreateProductImageUploadRequest request) {
        return ResponseEntity.ok(productImageStorageService.createUploadUrl(request));
    }
}
