package com.primecart.client;

import com.primecart.dto.request.CreatePaymentRequest;
import com.primecart.dto.response.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "payment-service",
        url = "http://localhost:8085"
)
public interface PaymentClient {

    @PostMapping("/api/payments")
    PaymentResponse createPayment(
            @RequestBody CreatePaymentRequest request
    );
}