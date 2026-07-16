package com.primecart.service;

import com.primecart.entity.Order;
import com.primecart.entity.OrderStatus;
import com.primecart.exception.ResourceNotFoundException;
import com.primecart.messaging.entity.ProcessedEvent;
import com.primecart.messaging.events.*;
import com.primecart.messaging.repository.ProcessedEventRepository;
import com.primecart.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderSagaService {

    private final OrderRepository orderRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void handleInventoryReserved(InventoryReservedEvent event) {

        //-------------------------------------------------------------
        // Idempotency
        //-------------------------------------------------------------
        if (processedEventRepository.existsByEventId(event.eventId())) {
            log.info("InventoryReservedEvent already processed. eventId={}, orderId={}", event.eventId(), event.orderId());
            return;
        }

        //-------------------------------------------------------------
        // Lock Order
        //-------------------------------------------------------------
        Order order = orderRepository
                .findByIdForUpdate(event.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + event.orderId()));

        //-------------------------------------------------------------
        // Validate State
        //-------------------------------------------------------------
        if (order.getStatus() != OrderStatus.PENDING) {

            log.warn("Ignoring InventoryReservedEvent. orderId={}, currentStatus={}, eventId={}", order.getId(), order.getStatus(),
                     event.eventId());

            saveProcessedEvent(event.eventId(), event.eventType());

            return;
        }

        //-------------------------------------------------------------
        // Update Order Status
        //-------------------------------------------------------------
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        order.setUpdatedAt(LocalDateTime.now());

        //-------------------------------------------------------------
        // Mark Inventory Event Processed
        //-------------------------------------------------------------
        saveProcessedEvent(event.eventId(), event.eventType());

        //-------------------------------------------------------------
        // Create Payment Requested Event
        //-------------------------------------------------------------
        PaymentRequestedEvent paymentRequestedEvent = new PaymentRequestedEvent(UUID.randomUUID(), "PAYMENT_REQUESTED", 1, order.getId(),
                                                                                order.getCustomerId(), order.getTotalAmount(), "CARD",
                                                                                Instant.now());

        //-------------------------------------------------------------
        // Publish Internal Spring Event
        //-------------------------------------------------------------
        applicationEventPublisher.publishEvent(paymentRequestedEvent);

        log.info("Inventory reserved and payment requested. " + "orderId={}, status={}, reservationId={}, paymentEventId={}", order.getId(),
                 order.getStatus(), event.reservationId(), paymentRequestedEvent.eventId());
    }

    @Transactional
    public void handleInventoryReservationFailed(InventoryReservationFailedEvent event) {

        //-------------------------------------------------------------
        // Idempotency
        //-------------------------------------------------------------
        if (processedEventRepository.existsByEventId(event.eventId())) {

            log.info("InventoryReservationFailedEvent already processed. eventId={}, orderId={}", event.eventId(), event.orderId());

            return;
        }

        //-------------------------------------------------------------
        // Lock Order
        //-------------------------------------------------------------
        Order order = orderRepository
                .findByIdForUpdate(event.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + event.orderId()));

        //-------------------------------------------------------------
        // Validate State
        //-------------------------------------------------------------
        if (order.getStatus() != OrderStatus.PENDING) {

            log.warn("Ignoring InventoryReservationFailedEvent. " + "orderId={}, currentStatus={}, eventId={}", order.getId(),
                     order.getStatus(), event.eventId());

            saveProcessedEvent(event.eventId(), event.eventType());

            return;
        }

        //-------------------------------------------------------------
        // Update Order Status
        //-------------------------------------------------------------
        order.setStatus(OrderStatus.INVENTORY_REJECTED);
        order.setUpdatedAt(LocalDateTime.now());

        //-------------------------------------------------------------
        // Mark Inventory Event Processed
        //-------------------------------------------------------------
        saveProcessedEvent(event.eventId(), event.eventType());

        log.warn("Order rejected due to inventory failure. " + "orderId={}, status={}, reason={}", order.getId(), order.getStatus(),
                 event.reason());
    }

    @Transactional
    public void handlePaymentCompleted(PaymentCompletedEvent event) {

        if (processedEventRepository.existsByEventId(event.eventId())) {

            log.info("PaymentCompletedEvent already processed. eventId={}, orderId={}", event.eventId(), event.orderId());

            return;
        }

        Order order = orderRepository
                .findByIdForUpdate(event.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + event.orderId()));

        if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {

            log.warn("Ignoring PaymentCompletedEvent. orderId={}, currentStatus={}, eventId={}", order.getId(), order.getStatus(),
                     event.eventId());

            saveProcessedEvent(event.eventId(), event.eventType());

            return;
        }

        order.setStatus(OrderStatus.CONFIRMED);
        order.setUpdatedAt(LocalDateTime.now());

        saveProcessedEvent(event.eventId(), event.eventType());

        log.info("Order confirmed. orderId={}, paymentId={}, paymentNumber={}", order.getId(), event.paymentId(), event.paymentNumber());
    }

    @Transactional
    public void handlePaymentFailed(PaymentFailedEvent event) {

        if (processedEventRepository.existsByEventId(event.eventId())) {

            log.info("PaymentFailedEvent already processed. eventId={}, orderId={}", event.eventId(), event.orderId());

            return;
        }

        Order order = orderRepository
                .findByIdForUpdate(event.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + event.orderId()));

        if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {

            log.warn("Ignoring PaymentFailedEvent. orderId={}, currentStatus={}, eventId={}", order.getId(), order.getStatus(),
                     event.eventId());

            saveProcessedEvent(event.eventId(), event.eventType());

            return;
        }

        order.setStatus(OrderStatus.PAYMENT_FAILED);
        order.setUpdatedAt(LocalDateTime.now());

        saveProcessedEvent(event.eventId(), event.eventType());

        InventoryReleaseRequestedEvent releaseEvent = new InventoryReleaseRequestedEvent(UUID.randomUUID(), "INVENTORY_RELEASE_REQUESTED",
                                                                                         1, order.getId(), event.reason(), Instant.now());

        applicationEventPublisher.publishEvent(releaseEvent);

        log.warn("Payment failed and inventory release requested. orderId={}, reason={}, releaseEventId={}", order.getId(), event.reason(),
                 releaseEvent.eventId());
    }

    private void saveProcessedEvent(UUID eventId, String eventType) {

        ProcessedEvent processedEvent = ProcessedEvent
                .builder()
                .eventId(eventId)
                .eventType(eventType)
                .processedAt(LocalDateTime.now())
                .build();

        processedEventRepository.save(processedEvent);
    }
}