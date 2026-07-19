package com.primecart.service;

import com.primecart.client.OrderClient;
import com.primecart.dto.request.CreatePaymentRequest;
import com.primecart.dto.response.PaymentResponse;
import com.primecart.entity.Payment;
import com.primecart.entity.PaymentStatus;
import com.primecart.exception.ResourceNotFoundException;
import com.primecart.mapper.PaymentMapper;
import com.primecart.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderClient orderClient;

    private String getCurrentUserId() {

        Jwt jwt = (Jwt) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return jwt.getSubject();
    }

    @PreAuthorize("hasRole('PAYMENT_CREATE')")
    @Override
    public PaymentResponse createPayment(CreatePaymentRequest request) {

        Payment payment = Payment
                .builder()
                .paymentNumber(generatePaymentNumber())
                .orderId(request.getOrderId())
                .customerId(getCurrentUserId())
                .amount(request.getAmount())
                .method(request.getMethod())
                .status(PaymentStatus.PENDING)
                .build();

        return paymentMapper.toResponse(paymentRepository.save(payment));
    }

    @PreAuthorize("hasRole('PAYMENT_READ')")
    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long id) {

        Payment payment = paymentRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        validatePaymentOwner(payment);
        return paymentMapper.toResponse(payment);
    }

    @PreAuthorize("hasRole('PAYMENT_READ')")
    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrder(Long orderId) {

        Payment payment = paymentRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        validatePaymentOwner(payment);
        return paymentMapper.toResponse(payment);
    }

    @PreAuthorize("hasRole('PAYMENT_SUCCESS')")
    @Override
    public PaymentResponse markSuccess(Long id) {

        Payment payment = paymentRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        validatePaymentOwner(payment);
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Only pending payments can be marked successful");
        }
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId(UUID
                                         .randomUUID()
                                         .toString());

        Payment saved = paymentRepository.save(payment);
        orderClient.paymentSuccess(payment.getOrderId());
        return paymentMapper.toResponse(saved);
    }

    @PreAuthorize("hasRole('PAYMENT_FAIL')")
    @Override
    public PaymentResponse markFailed(Long id) {

        Payment payment = paymentRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        validatePaymentOwner(payment);
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Only pending payments can be marked failed");
        }

        payment.setStatus(PaymentStatus.FAILED);
        Payment saved = paymentRepository.save(payment);
        orderClient.paymentFailed(payment.getOrderId());
        return paymentMapper.toResponse(saved);
    }

    @Override
    @PreAuthorize("hasRole('PAYMENT_RETRY')")
    public PaymentResponse retryPayment(Long id) {

        Payment payment = paymentRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        validatePaymentOwner(payment);

        if (payment.getStatus() != PaymentStatus.FAILED) {
            throw new IllegalStateException("Only failed payments can be retried");
        }

        orderClient.retryPayment(payment.getOrderId());

        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId(null);

        return paymentMapper.toResponse(paymentRepository.save(payment));
    }

    private String generatePaymentNumber() {

        return "PAY-" + System.currentTimeMillis();
    }

    private String generateTransactionId() {

        return UUID
                .randomUUID()
                .toString();
    }

    private void validatePaymentOwner(Payment payment) {

        var authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        boolean admin = authentication
                .getAuthorities()
                .stream()
                .anyMatch(authority -> authority
                        .getAuthority()
                        .equals("ROLE_ADMIN"));

        if (admin) {
            return;
        }

        String currentUserId = getCurrentUserId();

        if (!payment
                .getCustomerId()
                .equals(currentUserId)) {
            throw new ResourceNotFoundException("Payment not found");
        }
    }
}