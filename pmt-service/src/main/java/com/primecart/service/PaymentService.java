package com.primecart.service;

import com.primecart.dto.request.CreatePaymentRequest;
import com.primecart.dto.response.PaymentResponse;

public interface PaymentService {

    PaymentResponse createPayment(CreatePaymentRequest request);

    PaymentResponse getPayment(Long id);

    PaymentResponse getPaymentByOrder(Long orderId);

    PaymentResponse markSuccess(Long id);

    PaymentResponse markFailed(Long id);
}