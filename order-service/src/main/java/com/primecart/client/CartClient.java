package com.primecart.client;

import com.primecart.dto.response.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "cart-service", url = "http://localhost:8083")
public interface CartClient {

    @GetMapping("/api/cart")
    CartResponse getCart();

    @DeleteMapping("/api/cart")
    void clearCart();
}