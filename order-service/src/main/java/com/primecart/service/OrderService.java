package com.primecart.service;

import com.primecart.dto.request.CreateOrderRequest;
import com.primecart.dto.response.OrderResponse;
import com.primecart.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse getOrderById(Long id);

    OrderResponse getOrderByOrderNumber(String orderNumber);

    Page<OrderResponse> getAllOrders(Pageable pageable);

    List<OrderResponse> getOrdersByCustomerId(String customerId);

    Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable);

    void deleteOrder(Long id);
}