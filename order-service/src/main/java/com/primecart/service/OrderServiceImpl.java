package com.primecart.service;

import com.primecart.client.CartClient;
import com.primecart.client.InventoryClient;
import com.primecart.client.ProductClient;
import com.primecart.dto.request.ReserveStockRequest;
import com.primecart.dto.response.CartItemResponse;
import com.primecart.dto.response.CartResponse;
import com.primecart.dto.response.OrderItemResponse;
import com.primecart.dto.response.OrderResponse;
import com.primecart.entity.Order;
import com.primecart.entity.OrderItem;
import com.primecart.entity.OrderStatus;
import com.primecart.exception.CartEmptyException;
import com.primecart.exception.InventoryReservationException;
import com.primecart.exception.OrderNotFoundException;
import com.primecart.mapper.OrderMapper;
import com.primecart.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final InventoryClient inventoryClient;
    private final ProductClient productClient;
    private final CartClient cartClient;

    /**
     * Logged-in customer.
     */
    private String getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext()
                                     .getAuthentication();

        JwtAuthenticationToken token =
                (JwtAuthenticationToken) authentication;

        return token.getToken().getSubject();
    }

    @Override
    public Page<OrderResponse> getMyOrders(Pageable pageable) {

        String userId = getCurrentUserId();

        return orderRepository
                .findByCustomerId(userId, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional
    public OrderResponse createOrder() {

        log.info("Creating order for customer {}", getCurrentUserId());

        //-------------------------------------------------------------
        // Fetch Cart
        //-------------------------------------------------------------
        CartResponse cart = cartClient.getCart();

        if (cart == null || cart.getItems().isEmpty()) {
            throw new CartEmptyException("Cart is empty.");
        }

        //-------------------------------------------------------------
        // Create Order
        //-------------------------------------------------------------
        Order order = new Order();

        order.setOrderNumber(generateOrderNumber());
        order.setCustomerId(getCurrentUserId());
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal totalAmount = BigDecimal.ZERO;

        //-------------------------------------------------------------
        // Convert Cart Items to Order Items
        //-------------------------------------------------------------
        for (CartItemResponse cartItem : cart.getItems()) {

            reserveInventory(cartItem);

            OrderItem orderItem = new OrderItem();

            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());

            BigDecimal subtotal = calculateSubtotal(
                    cartItem.getPrice(),
                    cartItem.getQuantity());

            orderItem.setSubtotal(subtotal);

            order.addItem(orderItem);

            totalAmount = totalAmount.add(subtotal);
        }

        //-------------------------------------------------------------
        // Save Order
        //-------------------------------------------------------------
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        log.info("Order {} created successfully",
                savedOrder.getOrderNumber());

        //-------------------------------------------------------------
        // Clear Cart
        //-------------------------------------------------------------
        cartClient.clearCart();

        return OrderResponse.from(savedOrder);
    }

    /**
     * Reserve inventory.
     */
    private void reserveInventory(CartItemResponse cartItem) {

        try {

            inventoryClient.reserveStock(

                    new ReserveStockRequest(
                            cartItem.getProductId(),
                            cartItem.getQuantity()
                    )
            );
        } catch (Exception ex) {

            log.error("Inventory reservation failed for product {}",
                    cartItem.getProductId());

            throw new InventoryReservationException(
                    "Unable to reserve inventory for product "
                            + cartItem.getProductId()
            );
        }
    }

    /**
     * Calculate subtotal.
     */
    private BigDecimal calculateSubtotal(BigDecimal price,
                                         Integer quantity) {

        return price.multiply(
                BigDecimal.valueOf(quantity));
    }

    /**
     * Generate order number.
     */
    private String generateOrderNumber() {

        return "ORD-"
                + UUID.randomUUID()
                      .toString()
                      .replace("-", "")
                      .substring(0, 12)
                      .toUpperCase();
    }

//    @Transactional
//    @Override
//    public OrderResponse createOrder(CreateOrderRequest request) {
//
//        // Reserve stock first
//        for (OrderItemRequest itemRequest : request.getItems()) {
//
//            ReserveStockRequest reserve =
//                    new ReserveStockRequest();
//
//            reserve.setProductId(
//                    itemRequest.getProductId()
//            );
//
//            reserve.setQuantity(
//                    itemRequest.getQuantity()
//            );
//
//            inventoryClient.reserveStock(reserve);
//
//        }
//
//        Order order = Order.builder()
//                           .orderNumber(
//                                   UUID.randomUUID()
//                                       .toString()
//                           )
//                           .customerId(
//                                   getCurrentUserId()
//                           )
//                           .status(
//                                   OrderStatus.CREATED
//                           )
//                           .totalAmount(
//                                   BigDecimal.ZERO
//                           )
//                           .build();
//
//        BigDecimal totalAmount =
//                BigDecimal.ZERO;
//
//        for (OrderItemRequest requestItem : request.getItems()) {
//
//            // Get product details
//            ProductResponse product =
//                    productClient.getProduct(
//                            requestItem.getProductId()
//                    );
//
//            BigDecimal subtotal =
//                    product.getPrice()
//                           .multiply(
//                                   BigDecimal.valueOf(
//                                           requestItem.getQuantity()
//                                   )
//                           );
//
//            OrderItem orderItem =
//                    OrderItem.builder()
//                             .productId(
//                                     product.getId()
//                             )
//                             .productName(
//                                     product.getName()
//                             )
//                             .price(
//                                     product.getPrice()
//                             )
//                             .quantity(
//                                     requestItem.getQuantity()
//                             )
//                             .subtotal(
//                                     subtotal
//                             )
//                             .build();
//
//            order.addItem(orderItem);
//
//            totalAmount =
//                    totalAmount.add(subtotal);
//
//        }
//
//        order.setTotalAmount(totalAmount);
//
//        Order saved =
//                orderRepository.save(order);
//
//        return mapToResponse(saved);
//    }

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
    public List<OrderResponse> getOrdersByCustomerId(String customerId) {

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

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                                     .orElseThrow(() -> new RuntimeException("Order not found"));

        // Verify owner
        if (!order.getCustomerId().equals(getCurrentUserId())) {
            throw new RuntimeException("You are not authorized to cancel this order");
        }

        // Only CREATED orders can be cancelled
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new RuntimeException(
                    "Only CREATED orders can be cancelled"
            );
        }

        // Release reserved inventory
        for (OrderItem item : order.getItems()) {

            ReserveStockRequest request = new ReserveStockRequest();

            request.setProductId(item.getProductId());
            request.setQuantity(item.getQuantity());

            inventoryClient.releaseStock(request);
        }

        // Update status
        order.setStatus(OrderStatus.CANCELLED);

        Order updatedOrder = orderRepository.save(order);

        return mapToResponse(updatedOrder);
    }

    private OrderResponse mapToResponse(Order order) {

        List<OrderItemResponse> items =
                order.getItems()
                     .stream()
                     .map(item ->
                             OrderItemResponse.builder()
                                              .productId(item.getProductId())
                                              .productName(item.getProductName())
                                              .price(item.getPrice())
                                              .quantity(item.getQuantity())
                                              .subtotal(item.getSubtotal())
                                              .build()
                     )
                     .toList();

        return OrderResponse.builder()
                            .id(order.getId())
                            .orderNumber(order.getOrderNumber())
                            .customerId(order.getCustomerId())
                            .status(order.getStatus().toString())
                            .totalAmount(order.getTotalAmount())
                            .items(items)
                            .createdAt(order.getCreatedAt())
                            .build();
    }
}