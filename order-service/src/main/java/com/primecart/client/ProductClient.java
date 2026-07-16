package com.primecart.client;

import com.primecart.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service",
             url = "http://localhost:8081")
public interface ProductClient {

    @GetMapping("/api/products/{id}") ProductResponse getProduct(@PathVariable Long id);
}