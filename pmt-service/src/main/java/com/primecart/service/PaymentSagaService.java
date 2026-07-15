package com.primecart.service;

import com.primecart.entity.Payment;
import com.primecart.entity.PaymentMethod;
import com.primecart.entity.PaymentStatus;
import com.primecart.messaging.entity.ProcessedEvent;
import com.primecart.messaging.events.PaymentCompletedEvent;
import com.primecart.messaging.events.PaymentRequestedEvent;
import com.primecart.messaging.repository.ProcessedEventRepository;
import com.primecart.repository.PaymentRepository;
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
public class PaymentSagaService {

    private final PaymentRepository paymentRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void processPayment(PaymentRequestedEvent event) {

        if (processedEventRepository.existsByEventId(event.eventId())) {

            log.info("PaymentRequestedEvent already processed. eventId={}, orderId={}", event.eventId(), event.orderId());

            return;
        }

        Payment existingPayment = paymentRepository
                .findByOrderId(event.orderId())
                .orElse(null);

        if (existingPayment != null) {

            saveProcessedEvent(event);

            log.info("Payment already exists for order. orderId={}, paymentId={}", event.orderId(), existingPayment.getId());

            return;
        }

        Payment payment = new Payment();

        payment.setPaymentNumber(generatePaymentNumber());
        payment.setOrderId(event.orderId());
        payment.setAmount(event.amount());
        payment.setCustomerId(event.customerId());
        payment.setMethod(PaymentMethod.valueOf(event.paymentMethod()));
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        saveProcessedEvent(event);

        PaymentCompletedEvent completedEvent = new PaymentCompletedEvent(UUID.randomUUID(), "PAYMENT_COMPLETED", 1,
                savedPayment.getOrderId(), savedPayment.getId(), savedPayment.getPaymentNumber(), savedPayment.getAmount(), Instant.now());

        applicationEventPublisher.publishEvent(completedEvent);

        log.info("Payment completed. orderId={}, paymentId={}, paymentNumber={}", savedPayment.getOrderId(), savedPayment.getId(),
                savedPayment.getPaymentNumber());
    }

    private void saveProcessedEvent(PaymentRequestedEvent event) {

        ProcessedEvent processedEvent = ProcessedEvent
                .builder()
                .eventId(event.eventId())
                .eventType(event.eventType())
                .processedAt(LocalDateTime.now())
                .build();

        processedEventRepository.save(processedEvent);
    }

    private String generatePaymentNumber() {

        return "PAY-" + UUID
                .randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }
}