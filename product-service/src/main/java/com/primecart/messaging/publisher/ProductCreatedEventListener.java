package com.primecart.messaging.publisher;

import com.primecart.messaging.events.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCreatedEventListener {

    private final ProductEventPublisher productEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductCreated(ProductCreatedEvent event) {

        log.info("Database transaction committed. Publishing product event: {}", event.eventId());

        productEventPublisher.publishProductCreated(event);
    }
}