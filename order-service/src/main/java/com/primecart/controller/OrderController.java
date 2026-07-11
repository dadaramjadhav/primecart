package com.primecart.controller;

import com.primecart.dto.response.OrderResponse;
import com.primecart.entity.OrderStatus;
import com.primecart.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder() {
        return orderService.createOrder();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id) {

        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/order-number/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByOrderNumber(
            @PathVariable String orderNumber) {

        return ResponseEntity.ok(orderService.getOrderByOrderNumber(orderNumber));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(
                orderService.getMyOrders(pageable)
        );
    }

//    @GetMapping
//    public ResponseEntity<Page<OrderResponse>> getAllOrders(
//            @ParameterObject
//            @PageableDefault(
//                    page = 0,
//                    size = 10,
//                    sort = "createdAt"
//            )
//            Pageable pageable) {
//
//        return ResponseEntity.ok(orderService.getAllOrders(pageable));
//    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerId(
            @PathVariable String customerId) {

        return ResponseEntity.ok(orderService.getOrdersByCustomerId(customerId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<OrderResponse>> getOrdersByStatus(
            @PathVariable OrderStatus status,
            @ParameterObject
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt"
            ) Pageable pageable) {

        return ResponseEntity.ok(orderService.getOrdersByStatus(status, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable Long id) {

        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.cancelOrder(id)
        );
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.confirmOrder(id)
        );
    }

    @PutMapping("/{orderId}/payment-failed")
    public ResponseEntity<OrderResponse> paymentFailed(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                orderService.paymentFailed(orderId)
        );
    }

    @PutMapping("/{orderId}/payment-success")
    public ResponseEntity<OrderResponse> paymentSuccess(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                orderService.paymentSuccess(orderId)
        );
    }
}