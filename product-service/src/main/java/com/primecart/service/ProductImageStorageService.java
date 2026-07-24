package com.primecart.service;

import com.primecart.dto.request.CreateProductImageUploadRequest;
import com.primecart.dto.response.ProductImageUploadResponse;

public interface ProductImageStorageService {

    ProductImageUploadResponse createUploadUrl(CreateProductImageUploadRequest request);
}
