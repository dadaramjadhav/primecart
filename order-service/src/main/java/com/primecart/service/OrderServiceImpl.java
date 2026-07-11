package com.primecart.service;

import com.primecart.dto.request.CreateOrderRequest;
import com.primecart.dto.response.OrderResponse;
import com.primecart.entity.Order;
import com.primecart.entity.OrderStatus;
import com.primecart.exception.OrderNotFoundException;
import com.primecart.mapper.OrderMapper;
import com.primecart.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {

        log.info("Creating order for customerId: {}", request.getCustomerId());

        throw new UnsupportedOperationException(
                "Create Order will be implemented after Product Service integration.");
    }

    @Override
    public OrderResponse getOrderById(Long id) {

        log.info("Fetching order with id: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found with id: " + id));

        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse getOrderByOrderNumber(String orderNumber) {

        log.info("Fetching order with order number: {}", orderNumber);

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with order number: " + orderNumber));

        return orderMapper.toResponse(order);
    }

    @Override
    public Page<OrderResponse> getAllOrders(Pageable pageable) {

        log.info("Fetching all orders");

        return orderRepository.findAll(pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    public List<OrderResponse> getOrdersByCustomerId(Long customerId) {

        log.info("Fetching orders for customerId: {}", customerId);

        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    public Page<OrderResponse> getOrdersByStatus(OrderStatus status,
                                                 Pageable pageable) {

        log.info("Fetching orders with status: {}", status);

        return orderRepository.findByStatus(status, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    public void deleteOrder(Long id) {

        log.info("Deleting order with id: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found with id: " + id));

        orderRepository.delete(order);

        log.info("Order deleted successfully with id: {}", id);
    }
}