package com.primecart.controller;

import com.primecart.dto.request.CreatePaymentRequest;
import com.primecart.dto.response.PaymentResponse;
import com.primecart.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid
            @RequestBody
            CreatePaymentRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        log.info("POST /api/payments - Create payment request received");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentService.createPayment(request, jwt.getSubject()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable
            Long id,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {

        log.info("GET /api/payments/{} - Get payment request received", id);

        return ResponseEntity.ok(paymentService.getPayment(id, jwt.getSubject(), isAdmin(authentication)));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrder(
            @PathVariable
            Long orderId,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {

        log.info("GET /api/payments/order/{} - Get payment by order request received", orderId);

        return ResponseEntity.ok(paymentService.getPaymentByOrder(orderId, jwt.getSubject(), isAdmin(authentication)));
    }

    @PutMapping("/{id}/success")
    public ResponseEntity<PaymentResponse> markSuccess(
            @PathVariable
            Long id,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {

        log.info("PUT /api/payments/{}/success - Mark payment success request received", id);

        return ResponseEntity.ok(paymentService.markSuccess(id, jwt.getSubject(), isAdmin(authentication)));
    }

    @PutMapping("/{id}/failed")
    public ResponseEntity<PaymentResponse> markFailed(
            @PathVariable
            Long id,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {

        log.info("PUT /api/payments/{}/failed - Mark payment failed request received", id);

        return ResponseEntity.ok(paymentService.markFailed(id, jwt.getSubject(), isAdmin(authentication)));
    }

    @PutMapping("/{id}/retry")
    public ResponseEntity<PaymentResponse> retryPayment(
            @PathVariable
            Long id,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {

        return ResponseEntity.ok(paymentService.retryPayment(id, jwt.getSubject(), isAdmin(authentication)));
    }

    private boolean isAdmin(Authentication authentication) {

        return authentication
                .getAuthorities()
                .stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
