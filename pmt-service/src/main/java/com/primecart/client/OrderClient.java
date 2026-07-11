package com.primecart.client;

import com.primecart.dto.response.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(
        name = "order-service",
        url = "http://localhost:8082"
)
public interface OrderClient {

    @PutMapping("/api/orders/{orderId}/confirm")
    void confirmOrder(
            @PathVariable Long orderId
    );

    @PutMapping(
            "/api/orders/{orderId}/payment-success"
    )
    OrderResponse paymentSuccess(
            @PathVariable Long orderId
    );
}