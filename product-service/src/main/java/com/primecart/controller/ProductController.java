package com.primecart.controller;

import com.primecart.dto.request.CreateProductRequest;
import com.primecart.dto.request.UpdateProductRequest;
import com.primecart.dto.response.ProductResponse;
import com.primecart.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;import org.springframework.data.domain.Page;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request) {

        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {

        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {

        return ResponseEntity.ok(productService.getProductById(id));
    }

//    @GetMapping
//    public ResponseEntity<Page<ProductResponse>> getAllProducts(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "id") String sortBy,
//            @RequestParam(defaultValue = "asc") String direction) {
//
//        return ResponseEntity.ok(
//                productService.getAllProducts(page, size, sortBy, direction));
//    }
    @GetMapping("/active")
    public ResponseEntity<List<ProductResponse>> getActiveProducts() {

        return ResponseEntity.ok(productService.getActiveProducts());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {

        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(

            @RequestParam(required = false) Long category,

            @RequestParam(required = false) Long brand,

            @RequestParam(required = false) Boolean active,

            @RequestParam(required = false) String keyword,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "id") String sortBy,

            @RequestParam(defaultValue = "asc") String direction) {

        return ResponseEntity.ok(
                productService.getProducts(
                        category,
                        brand,
                        active,
                        keyword,
                        page,
                        size,
                        sortBy,
                        direction));
    }
}