package com.primecart.service;

import com.primecart.client.CartClient;
import com.primecart.dto.request.ReserveStockRequest;
import com.primecart.dto.response.CartItemResponse;
import com.primecart.dto.response.CartResponse;
import com.primecart.dto.response.OrderItemResponse;
import com.primecart.dto.response.OrderResponse;
import com.primecart.entity.Order;
import com.primecart.entity.OrderItem;
import com.primecart.entity.OrderStatus;
import com.primecart.exception.CartEmptyException;
import com.primecart.exception.OrderNotFoundException;
import com.primecart.mapper.OrderMapper;
import com.primecart.messaging.events.OrderCreatedEvent;
import com.primecart.messaging.events.OrderItemEvent;
import com.primecart.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CartClient cartClient;
    private final InventoryIntegrationService inventoryIntegrationService;
    private final ApplicationEventPublisher applicationEventPublisher;

    private String getCurrentUserId() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;

        return token
                .getToken()
                .getSubject();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ORDER_READ_OWN')")
    @Override
    public Page<OrderResponse> getMyOrders(Pageable pageable) {

        String userId = getCurrentUserId();

        return orderRepository
                .findByCustomerId(userId, pageable)
                .map(orderMapper::toResponse);
    }

    @PreAuthorize("hasRole('ORDER_CREATE')")
    @Override
    @Transactional
    public OrderResponse createOrder() {

        String customerId = getCurrentUserId();

        log.info("Creating order for customer {}", customerId);

        CartResponse cart = cartClient.getCart();

        if (cart == null || cart.getItems() == null || cart
                .getItems()
                .isEmpty()) {

            throw new CartEmptyException("Cart is empty.");
        }

        Order order = new Order();

        order.setOrderNumber(generateOrderNumber());
        order.setCustomerId(customerId);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItemResponse cartItem : cart.getItems()) {

            OrderItem orderItem = new OrderItem();

            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());

            BigDecimal subtotal = calculateSubtotal(cartItem.getPrice(), cartItem.getQuantity());

            orderItem.setSubtotal(subtotal);

            order.addItem(orderItem);

            totalAmount = totalAmount.add(subtotal);
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(UUID.randomUUID(), "ORDER_CREATED", 1, savedOrder.getId(),
                                                        savedOrder.getOrderNumber(), savedOrder.getCustomerId(),
                                                        savedOrder.getTotalAmount(), mapItems(savedOrder.getItems()), Instant.now());

        //-------------------------------------------------------------
        // Publish In-Process Event
        //-------------------------------------------------------------
        applicationEventPublisher.publishEvent(event);

        log.info("Pending order created. orderId={}, orderNumber={}, eventId={}", savedOrder.getId(), savedOrder.getOrderNumber(),
                 event.eventId());

        return mapToResponse(savedOrder);
    }

    private List<OrderItemEvent> mapItems(List<OrderItem> orderItems) {

        return orderItems
                .stream()
                .map(orderItem -> new OrderItemEvent(orderItem.getProductId(), orderItem.getQuantity(), orderItem.getPrice()))
                .toList();
    }

    /**
     * Calculate subtotal.
     */
    private BigDecimal calculateSubtotal(BigDecimal price, Integer quantity) {

        return price.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Generate order number.
     */
    private String generateOrderNumber() {

        return "ORD-" + UUID
                .randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ORDER_READ_OWN', 'ORDER_READ_ALL')")
    @Override
    public OrderResponse getOrderById(Long id) {

        log.info("Fetching order with id: {}", id);

        Order order = orderRepository
                .findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ORDER_READ_OWN', 'ORDER_READ_ALL')")
    @Override
    public OrderResponse getOrderByOrderNumber(String orderNumber) {

        log.info("Fetching order with order number: {}", orderNumber);

        Order order = orderRepository
                .findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with order number: " + orderNumber));

        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ORDER_READ_ALL')")
    @Override
    public Page<OrderResponse> getAllOrders(Pageable pageable) {

        log.info("Fetching all orders");

        return orderRepository
                .findAll(pageable)
                .map(orderMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ORDER_READ_ALL')")
    @Override
    public List<OrderResponse> getOrdersByCustomerId(String customerId) {

        log.info("Fetching orders for customerId: {}", customerId);

        return orderRepository
                .findByCustomerId(customerId)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ORDER_READ_ALL')")
    @Override
    public Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {

        log.info("Fetching orders with status: {}", status);

        return orderRepository
                .findByStatus(status, pageable)
                .map(orderMapper::toResponse);
    }

    @PreAuthorize("hasRole('ORDER_DELETE')")
    @Override
    public void deleteOrder(Long id) {

        log.info("Deleting order with id: {}", id);

        Order order = orderRepository
                .findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

        orderRepository.delete(order);

        log.info("Order deleted successfully with id: {}", id);
    }

    @PreAuthorize("hasRole('ORDER_CANCEL')")
    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Verify owner
        if (!order
                .getCustomerId()
                .equals(getCurrentUserId())) {
            throw new RuntimeException("You are not authorized to cancel this order");
        }

        // Only CREATED orders can be cancelled
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new RuntimeException("Only CREATED orders can be cancelled");
        }

        // Release reserved inventory
        for (OrderItem item : order.getItems()) {

            ReserveStockRequest request = new ReserveStockRequest();

            request.setProductId(item.getProductId());
            request.setQuantity(item.getQuantity());

            inventoryIntegrationService.releaseStock(request);
        }

        // Update status
        order.setStatus(OrderStatus.CANCELLED);

        Order updatedOrder = orderRepository.save(order);

        return mapToResponse(updatedOrder);
    }

    @PreAuthorize("hasRole('ORDER_CONFIRM')")
    @Override
    @Transactional
    public OrderResponse confirmOrder(Long orderId) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Verify owner
        if (!order
                .getCustomerId()
                .equals(getCurrentUserId())) {
            throw new RuntimeException("You are not authorized to confirm this order");
        }

        // Only CREATED orders can be confirmed
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new RuntimeException("Only CREATED orders can be confirmed");
        }

        // Confirm stock for every item
        for (OrderItem item : order.getItems()) {

            ReserveStockRequest request = new ReserveStockRequest();

            request.setProductId(item.getProductId());
            request.setQuantity(item.getQuantity());

            inventoryIntegrationService.confirmStock(request);
        }

        // Update order status
        order.setStatus(OrderStatus.CONFIRMED);

        Order updatedOrder = orderRepository.save(order);

        return mapToResponse(updatedOrder);
    }

    private OrderResponse mapToResponse(Order order) {

        List<OrderItemResponse> items = order
                .getItems()
                .stream()
                .map(item -> OrderItemResponse
                        .builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        return OrderResponse
                .builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .status(order
                                .getStatus()
                                .toString())
                .totalAmount(order.getTotalAmount())
                .items(items)
                .createdAt(order.getCreatedAt())
                .build();
    }

    @PreAuthorize("hasRole('ORDER_PAYMENT_FAIL')")
    @Override
    @Transactional
    public OrderResponse paymentFailed(Long orderId) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.PAYMENT_FAILED) {
            return orderMapper.toResponse(order);
        }
        if (order.getStatus() != OrderStatus.PAYMENT_PENDING && order.getStatus() != OrderStatus.CREATED) {

            throw new IllegalStateException("Payment cannot fail for order in status: " + order.getStatus());
        }
        // Release inventory
        for (OrderItem item : order.getItems()) {

            ReserveStockRequest request = new ReserveStockRequest(item.getProductId(), item.getQuantity());

            inventoryIntegrationService.releaseStock(request);
        }

        order.setStatus(OrderStatus.PAYMENT_FAILED);
//        order.setStatus(OrderStatus.CANCELLED);

        return mapToResponse(orderRepository.save(order));
    }

    @PreAuthorize("hasRole('ORDER_PAYMENT_SUCCESS')")
    @Override
    @Transactional
    public OrderResponse paymentSuccess(Long orderId) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.CREATED && order.getStatus() != OrderStatus.PAYMENT_PENDING) {

            throw new RuntimeException("Invalid order status");
        }

        // confirm inventory
        for (OrderItem item : order.getItems()) {

            ReserveStockRequest request = new ReserveStockRequest(item.getProductId(), item.getQuantity());

            inventoryIntegrationService.confirmStock(request);
        }

        order.setStatus(OrderStatus.CONFIRMED);

        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @PreAuthorize("hasRole('ORDER_PAYMENT_RETRY')")
    @Transactional
    public OrderResponse retryPayment(Long orderId) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

//        validateOrderAccess(order);

        if (order.getStatus() != OrderStatus.PAYMENT_FAILED) {
            throw new IllegalStateException("Only payment-failed orders can retry payment");
        }

        for (OrderItem item : order.getItems()) {

            ReserveStockRequest request = new ReserveStockRequest(item.getProductId(), item.getQuantity());

            inventoryIntegrationService.reserveStock(request);
        }

        order.setStatus(OrderStatus.PAYMENT_PENDING);

        return orderMapper.toResponse(orderRepository.save(order));
    }

    private boolean hasAuthority(String authority) {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        return authentication != null && authentication
                .getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> grantedAuthority
                        .getAuthority()
                        .equals(authority));
    }

    private void validateOrderAccess(Order order) {

        if (hasAuthority("ROLE_ORDER_READ_ALL")) {
            return;
        }

        String currentUserId = getCurrentUserId();

        if (!order
                .getCustomerId()
                .equals(currentUserId)) {
            throw new OrderNotFoundException("Order not found");
        }
    }
}
