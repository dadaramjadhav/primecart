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

    @PreAuthorize("hasRole('PAYMENT_CREATE')")
    @Override
    public PaymentResponse createPayment(CreatePaymentRequest request, String currentUserId) {

        Payment payment = Payment
                .builder()
                .paymentNumber(generatePaymentNumber())
                .orderId(request.getOrderId())
                .customerId(currentUserId)
                .amount(request.getAmount())
                .method(request.getMethod())
                .status(PaymentStatus.PENDING)
                .build();

        return paymentMapper.toResponse(paymentRepository.save(payment));
    }

    @PreAuthorize("hasRole('PAYMENT_READ')")
    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long id, String currentUserId, boolean admin) {

        Payment payment = getPaymentEntity(id);
        validatePaymentOwner(payment, currentUserId, admin);
        return paymentMapper.toResponse(payment);
    }

    @PreAuthorize("hasRole('PAYMENT_READ')")
    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrder(Long orderId, String currentUserId, boolean admin) {

        Payment payment = getPaymentByOrderEntity(orderId);

        validatePaymentOwner(payment, currentUserId, admin);
        return paymentMapper.toResponse(payment);
    }

    @PreAuthorize("hasRole('PAYMENT_SUCCESS')")
    @Override
    public PaymentResponse markSuccess(Long id, String currentUserId, boolean admin) {

        Payment payment = getPaymentEntity(id);
        validatePaymentOwner(payment, currentUserId, admin);
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Only pending payments can be marked successful");
        }
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId(UUID
                                         .randomUUID()
                                         .toString());

        orderClient.paymentSuccess(payment.getOrderId());
        return paymentMapper.toResponse(payment);
    }

    @PreAuthorize("hasRole('PAYMENT_FAIL')")
    @Override
    public PaymentResponse markFailed(Long id, String currentUserId, boolean admin) {

        Payment payment = getPaymentEntity(id);
        validatePaymentOwner(payment, currentUserId, admin);
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Only pending payments can be marked failed");
        }

        payment.setStatus(PaymentStatus.FAILED);
        orderClient.paymentFailed(payment.getOrderId());
        return paymentMapper.toResponse(payment);
    }

    @Override
    @PreAuthorize("hasRole('PAYMENT_RETRY')")
    public PaymentResponse retryPayment(Long id, String currentUserId, boolean admin) {

        Payment payment = getPaymentEntity(id);

        validatePaymentOwner(payment, currentUserId, admin);

        if (payment.getStatus() != PaymentStatus.FAILED) {
            throw new IllegalStateException("Only failed payments can be retried");
        }

        orderClient.retryPayment(payment.getOrderId());

        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId(null);

        return paymentMapper.toResponse(payment);
    }

    private String generatePaymentNumber() {

        return "PAY-" + System.currentTimeMillis();
    }

    private Payment getPaymentEntity(Long id) {

        return paymentRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    private Payment getPaymentByOrderEntity(Long orderId) {

        return paymentRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    private void validatePaymentOwner(Payment payment, String currentUserId, boolean admin) {

        if (admin) {
            return;
        }

        if (!payment
                .getCustomerId()
                .equals(currentUserId)) {
            throw new ResourceNotFoundException("Payment not found");
        }
    }
}
