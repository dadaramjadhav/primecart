package com.primecart.messaging.consumer;

import com.primecart.messaging.RabbitMqConstants;
import com.primecart.messaging.events.PaymentFailedEvent;
import com.primecart.service.OrderSagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFailedEventConsumer {

    private final OrderSagaService orderSagaService;

    @RabbitListener(queues = RabbitMqConstants.ORDER_PAYMENT_FAILED_QUEUE)
    public void consume(PaymentFailedEvent event) {

        log.info("Received PaymentFailedEvent. eventId={}, orderId={}, paymentId={}, reason={}", event.eventId(), event.orderId(),
                event.paymentId(), event.reason());

        validate(event);

        orderSagaService.handlePaymentFailed(event);

        log.info("PaymentFailedEvent processed. eventId={}, orderId={}", event.eventId(), event.orderId());
    }

    private void validate(PaymentFailedEvent event) {

        if (event == null) {
            throw new IllegalArgumentException("PaymentFailedEvent is required");
        }

        if (event.eventId() == null) {
            throw new IllegalArgumentException("eventId is required");
        }

        if (!"PAYMENT_FAILED".equals(event.eventType())) {
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

        if (event.reason() == null || event
                .reason()
                .isBlank()) {

            throw new IllegalArgumentException("Failure reason is required");
        }
    }
}