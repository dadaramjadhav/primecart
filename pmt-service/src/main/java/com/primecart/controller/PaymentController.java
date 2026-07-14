package com.primecart.controller;

import com.primecart.dto.request.CreatePaymentRequest;
import com.primecart.dto.response.PaymentResponse;
import com.primecart.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {

        log.info("POST /api/payments - Create payment request received");

        return ResponseEntity.ok(
                paymentService.createPayment(request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable Long id) {

        log.info("GET /api/payments/{} - Get payment request received", id);

        return ResponseEntity.ok(
                paymentService.getPayment(id)
        );
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrder(
            @PathVariable Long orderId) {

        log.info("GET /api/payments/order/{} - Get payment by order request received",
                orderId);

        return ResponseEntity.ok(
                paymentService.getPaymentByOrder(orderId)
        );
    }

    @PutMapping("/{id}/success")
    public ResponseEntity<PaymentResponse> markSuccess(
            @PathVariable Long id) {

        log.info("PUT /api/payments/{}/success - Mark payment success request received",
                id);

        return ResponseEntity.ok(
                paymentService.markSuccess(id)
        );
    }

    @PutMapping("/{id}/failed")
    public ResponseEntity<PaymentResponse> markFailed(
            @PathVariable Long id) {

        log.info("PUT /api/payments/{}/failed - Mark payment failed request received",
                id);

        return ResponseEntity.ok(
                paymentService.markFailed(id)
        );
    }
}