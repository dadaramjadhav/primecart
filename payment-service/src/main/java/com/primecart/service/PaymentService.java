package com.primecart.service;

import com.primecart.dto.request.CreatePaymentRequest;
import com.primecart.dto.response.PaymentResponse;

public interface PaymentService {

    PaymentResponse createPayment(CreatePaymentRequest request, String currentUserId);

    PaymentResponse getPayment(Long id, String currentUserId, boolean admin);

    PaymentResponse getPaymentByOrder(Long orderId, String currentUserId, boolean admin);

    PaymentResponse markSuccess(Long id, String currentUserId, boolean admin);

    PaymentResponse markFailed(Long id, String currentUserId, boolean admin);

    PaymentResponse retryPayment(Long id, String currentUserId, boolean admin);
}
