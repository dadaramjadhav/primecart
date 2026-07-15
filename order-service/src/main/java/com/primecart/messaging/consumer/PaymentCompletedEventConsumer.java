package com.primecart.messaging.consumer;

import com.primecart.messaging.RabbitMqConstants;
import com.primecart.messaging.events.PaymentCompletedEvent;
import com.primecart.service.OrderSagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCompletedEventConsumer {

    private final OrderSagaService orderSagaService;

    @RabbitListener(queues = RabbitMqConstants.ORDER_PAYMENT_COMPLETED_QUEUE)
    public void consume(PaymentCompletedEvent event) {

        log.info("Received PaymentCompletedEvent. eventId={}, orderId={}, paymentId={}", event.eventId(), event.orderId(),
                event.paymentId());

        validate(event);

        orderSagaService.handlePaymentCompleted(event);

        log.info("PaymentCompletedEvent processed. eventId={}, orderId={}", event.eventId(), event.orderId());
    }

    private void validate(PaymentCompletedEvent event) {

        if (event == null) {
            throw new IllegalArgumentException("PaymentCompletedEvent is required");
        }

        if (event.eventId() == null) {
            throw new IllegalArgumentException("eventId is required");
        }

        if (!"PAYMENT_COMPLETED".equals(event.eventType())) {
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

        if (event.paymentId() == null) {
            throw new IllegalArgumentException("paymentId is required");
        }
    }
}