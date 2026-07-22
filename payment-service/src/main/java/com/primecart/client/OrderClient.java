package com.primecart.client;

import com.primecart.config.OrderClientCredentialsFeignConfig;
import com.primecart.dto.response.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "order-service",
             url = "http://localhost:8082",
             configuration = OrderClientCredentialsFeignConfig.class)
public interface OrderClient {

    @PutMapping("/api/orders/{orderId}/payment-success")
    OrderResponse paymentSuccess(
            @PathVariable
            Long orderId);

    @PutMapping("/api/orders/{orderId}/payment-failed")
    OrderResponse paymentFailed(
            @PathVariable
            Long orderId);

    @PutMapping("/api/orders/{orderId}/retry-payment")
    OrderResponse retryPayment(
            @PathVariable
            Long orderId);
}