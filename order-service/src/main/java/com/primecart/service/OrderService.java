package com.primecart.service;

import com.primecart.dto.response.OrderResponse;
import com.primecart.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder();

    OrderResponse getOrderById(Long id);

    OrderResponse getOrderByOrderNumber(String orderNumber);

    Page<OrderResponse> getAllOrders(Pageable pageable);

    List<OrderResponse> getOrdersByCustomerId(String customerId);

    Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable);

    void deleteOrder(Long id);

    Page<OrderResponse> getMyOrders(Pageable pageable);

    OrderResponse cancelOrder(Long orderId);

    OrderResponse confirmOrder(Long orderId);

    OrderResponse paymentFailed(Long orderId);

    OrderResponse paymentSuccess(Long orderId);
}