package com.primecart.controller;

import com.primecart.dto.request.CreateProductRequest;
import com.primecart.dto.request.UpdateProductRequest;
import com.primecart.dto.response.ProductResponse;
import com.primecart.metrics.ProductMetrics;
import com.primecart.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMetrics productMetrics;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid
            @RequestBody
            CreateProductRequest request
    ) {

        log.info("POST /api/products - Create product request received: categoryId={}, brandId={}, active={}",
                 request.categoryId(),
                 request.brandId(),
                 request.active());

        ProductResponse response = productService.createProduct(request);
        log.info("POST /api/products - Product created successfully: productId={}",
                 response.id());

        // Count only successful product creation
        productMetrics.incrementProductCreated();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable
            Long id,
            @Valid
            @RequestBody
            UpdateProductRequest request
    ) {

        log.info("PUT /api/products/{} - Update product request received: categoryId={}, brandId={}, active={}",
                 id,
                 request.categoryId(),
                 request.brandId(),
                 request.active());

        ProductResponse response = productService.updateProduct(id,
                                                                request);
        log.info("PUT /api/products/{} - Product updated successfully",
                 id);

        // Count only successful product update
        productMetrics.incrementProductUpdated();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable
            Long id
    ) {

        log.info("GET /api/products/{} - Get product by id request received",
                 id);

        ProductResponse response = productService.getProductById(id);
        log.info("GET /api/products/{} - Product retrieved successfully",
                 id);

        // Count only when the product was found successfully
        productMetrics.incrementProductView();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ProductResponse>> getActiveProducts() {

        log.info("GET /api/products/active - Get active products request received");

        List<ProductResponse> products = productService.getActiveProducts();
        log.info("GET /api/products/active - Active products retrieved successfully: count={}",
                 products.size());
        productMetrics.incrementActiveProductsView();

        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable
            Long id
    ) {

        log.info("DELETE /api/products/{} - Delete product request received",
                 id);

        productService.deleteProduct(id);
        log.info("DELETE /api/products/{} - Product deleted successfully",
                 id);
        productMetrics.incrementProductDeleted();

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(required = false)
            Long category,
            @RequestParam(required = false)
            Long brand,
            @RequestParam(required = false)
            Boolean active,
            @RequestParam(required = false)
            String keyword,
            @RequestParam(defaultValue = "0")
            @Min(value = 0,
                 message = "Page must be zero or greater")
            int page,

            @RequestParam(defaultValue = "20")
            @Min(value = 1,
                 message = "Size must be at least 1")
            @Max(value = 100,
                 message = "Size cannot exceed 100")
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            @Pattern(regexp = "^(?i)(asc|desc)$",
                     message = "Direction must be asc or desc")
            String direction
    ) {

        log.info("GET /api/products - Get products request received: category={}, brand={}, active={}, " + "keywordPresent={}, page={}, size={}, sortBy={}, direction={}",
                 category,
                 brand,
                 active,
                 keyword != null && !keyword.isBlank(),
                 page,
                 size,
                 sortBy,
                 direction);

        Page<ProductResponse> products = productService.getProducts(category,
                                                                    brand,
                                                                    active,
                                                                    keyword,
                                                                    page,
                                                                    size,
                                                                    sortBy,
                                                                    direction);

        log.info("GET /api/products - Products retrieved successfully: returned={}, totalElements={}, totalPages={}",
                 products.getNumberOfElements(),
                 products.getTotalElements(),
                 products.getTotalPages());
//        int a = 10;
//        if (a == 10) throw new RuntimeException("product error.");
        return ResponseEntity.ok(products);
    }
}
