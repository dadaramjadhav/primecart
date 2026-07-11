package com.primecart.service;

import com.primecart.dto.request.CreateProductRequest;
import com.primecart.dto.request.UpdateProductRequest;
import com.primecart.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse updateProduct(Long id, UpdateProductRequest request);

    ProductResponse getProductById(Long id);

    Page<ProductResponse> getProducts(
            Long category,
            Long brand,
            Boolean active,
            String keyword,
            int page,
            int size,
            String sortBy,
            String direction);

    List<ProductResponse> getActiveProducts();

    void deleteProduct(Long id);
}