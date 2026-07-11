package com.primecart.controller;

import com.primecart.dto.request.CreatePaymentRequest;
import com.primecart.dto.response.PaymentResponse;
import com.primecart.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {

        return ResponseEntity.ok(
                paymentService.createPayment(request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                paymentService.getPayment(id)
        );
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrder(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                paymentService.getPaymentByOrder(orderId)
        );
    }

    @PutMapping("/{id}/success")
    public ResponseEntity<PaymentResponse> markSuccess(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                paymentService.markSuccess(id)
        );
    }

    @PutMapping("/{id}/failed")
    public ResponseEntity<PaymentResponse> markFailed(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                paymentService.markFailed(id)
        );
    }
}