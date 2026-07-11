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

    @Override
    public PaymentResponse createPayment(CreatePaymentRequest request) {

        Payment payment = Payment.builder()
                                 .paymentNumber(generatePaymentNumber())
                                 .orderId(request.getOrderId())
                                 .customerId(getCurrentUserId())
                                 .amount(request.getAmount())
                                 .method(request.getMethod())
                                 .status(PaymentStatus.PENDING)
                                 .build();

        return paymentMapper.toResponse(
                paymentRepository.save(payment)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long id) {

        Payment payment = paymentRepository.findById(id)
                                           .orElseThrow(() ->
                                                   new ResourceNotFoundException("Payment not found"));

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrder(Long orderId) {

        Payment payment = paymentRepository.findByOrderId(orderId)
                                           .orElseThrow(() ->
                                                   new ResourceNotFoundException("Payment not found"));

        return paymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse markSuccess(Long id) {

        Payment payment =
                paymentRepository.findById(id)
                                 .orElseThrow(() ->
                                         new ResourceNotFoundException(
                                                 "Payment not found"));
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId(UUID.randomUUID().toString());

        Payment saved = paymentRepository.save(payment);

        // notify order service
//        orderClient.confirmOrder(payment.getOrderId());
        orderClient.paymentSuccess(payment.getOrderId());
        return paymentMapper.toResponse(saved);
    }

    @Override
    public PaymentResponse markFailed(Long id) {

        Payment payment = paymentRepository.findById(id)
                                           .orElseThrow(() ->
                                                   new ResourceNotFoundException("Payment not found"));

        payment.setStatus(PaymentStatus.FAILED);

        return paymentMapper.toResponse(
                paymentRepository.save(payment)
        );
    }

    private String generatePaymentNumber() {

        return "PAY-" + System.currentTimeMillis();
    }

    private String generateTransactionId() {

        return UUID.randomUUID().toString();
    }
}