package com.primecart.messaging.consumer;

import com.primecart.messaging.RabbitMqConstants;
import com.primecart.messaging.events.PaymentRequestedEvent;
import com.primecart.service.PaymentSagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestedEventConsumer {

    private final PaymentSagaService paymentSagaService;

    @RabbitListener(queues = RabbitMqConstants.PAYMENT_REQUESTED_QUEUE)
    public void consume(PaymentRequestedEvent event) {

        log.info("Received PaymentRequestedEvent. eventId={}, orderId={}, amount={}", event.eventId(), event.orderId(), event.amount());

        validate(event);

        paymentSagaService.processPayment(event);

        log.info("PaymentRequestedEvent processing completed. eventId={}, orderId={}", event.eventId(), event.orderId());
    }

    private void validate(PaymentRequestedEvent event) {

        if (event == null) {
            throw new IllegalArgumentException("PaymentRequestedEvent is required");
        }

        if (event.eventId() == null) {
            throw new IllegalArgumentException("eventId is required");
        }

        if (!"PAYMENT_REQUESTED".equals(event.eventType())) {
            throw new IllegalArgumentException("Unsupported event type: " + event.eventType());
        }

        if (!Integer
                .valueOf(1)
                .equals(event.eventVersion())) {
            throw new IllegalArgumentException("Unsupported event version: " + event.eventVersion());
        }

        if (event.orderId() == null) {
            throw new IllegalArgumentException("orderId is required");
        }

        if (event.amount() == null || event
                .amount()
                .signum() <= 0) {

            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }

        if (event.paymentMethod() == null || event
                .paymentMethod()
                .isBlank()) {

            throw new IllegalArgumentException("paymentMethod is required");
        }
    }
}