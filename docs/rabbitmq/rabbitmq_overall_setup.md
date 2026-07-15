# RabbitMQ Setup for PrimeCart Microservices

## Overview

RabbitMQ has been introduced into PrimeCart to support asynchronous communication between microservices.

The first implemented scenario is:

```text
Product Service
      |
      | publishes product.created
      v
RabbitMQ
      |
      v
Inventory Service
creates inventory record
```

This setup demonstrates:

- Asynchronous communication
- Loose coupling
- Event-driven architecture
- Eventual consistency
- Reliable message delivery
- Retry and Dead Letter Queue handling
- Idempotent message consumption
- Publisher confirms
- RabbitMQ metrics
- Traceability using event IDs

This flow is intentionally simple and does not yet implement the Saga pattern.

---

# Why RabbitMQ Was Added

Without RabbitMQ, Product Service would call Inventory Service synchronously:

```text
Product Service
      |
      | REST / Feign
      v
Inventory Service
```

Problems with synchronous communication:

- Product Service depends on Inventory Service availability
- Product creation can fail when Inventory Service is down
- Services become tightly coupled
- Slow Inventory Service increases Product API latency

With RabbitMQ:

```text
Product Service
      |
      | product.created
      v
RabbitMQ Queue
      |
      v
Inventory Service
```

Benefits:

- Product Service does not wait for Inventory Service
- Inventory Service can be temporarily unavailable
- Messages remain in the queue until the consumer recovers
- Additional consumers can subscribe later
- Services become loosely coupled

---

# Current Architecture

```text
Client
  |
  v
API Gateway
  |
  v
Product Service
  |
  | Save Product
  | Publish ProductCreatedEvent
  v
RabbitMQ Topic Exchange
  |
  | Routing Key: product.created
  v
Inventory Product Created Queue
  |
  v
Inventory Service
  |
  | Validate Event
  | Check Duplicate
  | Create Inventory
  v
Inventory Database
```

---

# RabbitMQ Topology

## Main Exchange

```text
Name: primecart.events
Type: topic
Durable: true
Auto Delete: false
```

## Routing Key

```text
product.created
```

## Main Queue

```text
inventory.product-created.queue
```

## Dead Letter Exchange

```text
primecart.dead-letter
```

## Dead Letter Queue

```text
inventory.product-created.dlq
```

## Binding

```text
primecart.events
    |
    | product.created
    v
inventory.product-created.queue
```

DLQ flow:

```text
inventory.product-created.queue
    |
    | failed after retries
    v
primecart.dead-letter
    |
    v
inventory.product-created.dlq
```

---

# Docker Compose Setup

```yaml
services:

  rabbitmq:
    image: rabbitmq:4-management
    container_name: primecart-rabbitmq

    ports:
      - "5672:5672"
      - "15672:15672"

    environment:
      RABBITMQ_DEFAULT_USER: primecart
      RABBITMQ_DEFAULT_PASS: primecart123

    volumes:
      - rabbitmq-data:/var/lib/rabbitmq

    networks:
      - microservices-network

    restart: unless-stopped

volumes:
  rabbitmq-data:

networks:
  microservices-network:
    external: true
```

Start RabbitMQ:

```bash
docker compose up -d rabbitmq
```

RabbitMQ Management UI:

```text
http://localhost:15672
```

Credentials:

```text
Username: primecart
Password: primecart123
```

Ports:

| Port | Purpose |
|---|---|
| 5672 | AMQP communication |
| 15672 | RabbitMQ Management UI |

---

# Maven Dependency

Add to Product Service and Inventory Service:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

---

# RabbitMQ Connection Configuration

## Local Development

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: primecart
    password: primecart123
    virtual-host: /
```

## Docker Environment

```yaml
spring:
  rabbitmq:
    host: rabbitmq
    port: 5672
    username: primecart
    password: primecart123
```

## Environment Variable Based Configuration

```yaml
spring:
  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USERNAME:primecart}
    password: ${RABBITMQ_PASSWORD:primecart123}
    virtual-host: ${RABBITMQ_VIRTUAL_HOST:/}
```

---

# RabbitMQ Constants

```java
package com.primecart.messaging;

public final class RabbitMqConstants {

    private RabbitMqConstants() {
    }

    public static final String PRIME_CART_EXCHANGE =
            "primecart.events";

    public static final String PRODUCT_CREATED_ROUTING_KEY =
            "product.created";

    public static final String INVENTORY_PRODUCT_CREATED_QUEUE =
            "inventory.product-created.queue";

    public static final String DEAD_LETTER_EXCHANGE =
            "primecart.dead-letter";

    public static final String INVENTORY_PRODUCT_CREATED_DLQ =
            "inventory.product-created.dlq";
}
```

---

# Event Contract

## ProductCreatedEvent

```java
package com.primecart.messaging.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductCreatedEvent(

        UUID eventId,

        String eventType,

        Long productId,

        String sku,

        String productName,

        BigDecimal price,

        Integer initialStock,

        Instant occurredAt
) {
}
```

Example JSON:

```json
{
  "eventId": "d0e80a0a-f492-45d0-82d8-2d402eaea912",
  "eventType": "PRODUCT_CREATED",
  "productId": 7,
  "sku": "LAPTOP-007",
  "productName": "Laptop",
  "price": 85000,
  "initialStock": 10,
  "occurredAt": "2026-07-15T12:29:25Z"
}
```

Important fields:

- `eventId` for idempotency and tracing
- `eventType` for event identification
- `productId` as business entity identifier
- `sku` as product business key
- `occurredAt` as event timestamp

---

# JSON Message Conversion

Add to both Product Service and Inventory Service:

```java
package com.primecart.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqJsonConfig {

    @Bean
    public MessageConverter rabbitMessageConverter(
            ObjectMapper objectMapper) {

        return new JacksonJsonMessageConverter(objectMapper);
    }
}
```

Depending on the Spring AMQP version, the converter class may be:

```java
Jackson2JsonMessageConverter
```

or:

```java
JacksonJsonMessageConverter
```

---

# Product Service Producer Configuration

```java
package com.primecart.messaging.config;

import com.primecart.messaging.RabbitMqConstants;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqProducerConfig {

    @Bean
    public TopicExchange primeCartExchange() {

        return new TopicExchange(
                RabbitMqConstants.PRIME_CART_EXCHANGE,
                true,
                false
        );
    }
}
```

---

# Product Event Publisher

```java
package com.primecart.messaging.publisher;

import com.primecart.messaging.RabbitMqConstants;
import com.primecart.messaging.events.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishProductCreated(
            ProductCreatedEvent event) {

        CorrelationData correlationData =
                new CorrelationData(
                        event.eventId().toString()
                );

        log.info(
                "Publishing ProductCreatedEvent. eventId={}, productId={}",
                event.eventId(),
                event.productId()
        );

        rabbitTemplate.convertAndSend(
                RabbitMqConstants.PRIME_CART_EXCHANGE,
                RabbitMqConstants.PRODUCT_CREATED_ROUTING_KEY,
                event,
                correlationData
        );
    }
}
```

---

# Publish After Database Commit

Publishing after transaction commit prevents publishing an event for a product transaction that later rolls back.

Example listener:

```java
package com.primecart.messaging.publisher;

import com.primecart.messaging.events.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCreatedEventListener {

    private final ProductEventPublisher productEventPublisher;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(ProductCreatedEvent event) {

        log.info(
                "Database transaction committed. Publishing product event: {}",
                event.eventId()
        );

        productEventPublisher.publishProductCreated(event);
    }
}
```

Product Service publishes an application event during product creation:

```java
applicationEventPublisher.publishEvent(
        new ProductCreatedEvent(
                UUID.randomUUID(),
                "PRODUCT_CREATED",
                savedProduct.getId(),
                savedProduct.getSku(),
                savedProduct.getName(),
                savedProduct.getPrice(),
                savedProduct.getStock(),
                Instant.now()
        )
);
```

Important limitation:

```text
Database commit succeeds
Application crashes before RabbitMQ publish
Event may be lost
```

The Transactional Outbox Pattern should be added later to solve this gap.

---

# Publisher Confirms and Returned Messages

## Product Service Configuration

```yaml
spring:
  rabbitmq:
    publisher-confirm-type: correlated
    publisher-returns: true

    template:
      mandatory: true
```

## Callback Configuration

```java
package com.primecart.messaging.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RabbitPublisherConfirmConfig {

    private final RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void configureCallbacks() {

        rabbitTemplate.setConfirmCallback(
                (correlationData, ack, cause) -> {

                    if (ack) {
                        log.debug(
                                "RabbitMQ confirmed message. correlationData={}",
                                correlationData
                        );
                    } else {
                        log.error(
                                "RabbitMQ rejected message. " +
                                "correlationData={}, cause={}",
                                correlationData,
                                cause
                        );
                    }
                }
        );

        rabbitTemplate.setReturnsCallback(
                returned -> log.error(
                        "RabbitMQ returned unroutable message. " +
                                "exchange={}, routingKey={}, replyText={}",
                        returned.getExchange(),
                        returned.getRoutingKey(),
                        returned.getReplyText()
                )
        );
    }
}
```

## Meaning of Publisher Confirm

Publisher confirm tells Product Service whether RabbitMQ accepted the message.

## Meaning of Returned Message

When `mandatory=true`, RabbitMQ returns a message if it cannot route it to any queue.

Example:

```text
RabbitMQ returned unroutable message
exchange=primecart.events
routingKey=product.created
replyText=NO_ROUTE
```

Common causes:

- Inventory Service queue was not declared
- Binding key mismatch
- Exchange type mismatch
- Producer and consumer use different virtual hosts
- Inventory Service failed during startup

---

# Inventory Service Queue and DLQ Configuration

```java
package com.primecart.messaging.config;

import com.primecart.messaging.RabbitMqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConsumerConfig {

    @Bean
    public TopicExchange primeCartExchange() {

        return new TopicExchange(
                RabbitMqConstants.PRIME_CART_EXCHANGE,
                true,
                false
        );
    }

    @Bean
    public DirectExchange deadLetterExchange() {

        return new DirectExchange(
                RabbitMqConstants.DEAD_LETTER_EXCHANGE,
                true,
                false
        );
    }

    @Bean
    public Queue inventoryProductCreatedQueue() {

        return QueueBuilder
                .durable(
                        RabbitMqConstants
                                .INVENTORY_PRODUCT_CREATED_QUEUE
                )
                .deadLetterExchange(
                        RabbitMqConstants
                                .DEAD_LETTER_EXCHANGE
                )
                .deadLetterRoutingKey(
                        RabbitMqConstants
                                .INVENTORY_PRODUCT_CREATED_DLQ
                )
                .build();
    }

    @Bean
    public Queue inventoryProductCreatedDlq() {

        return QueueBuilder
                .durable(
                        RabbitMqConstants
                                .INVENTORY_PRODUCT_CREATED_DLQ
                )
                .build();
    }

    @Bean
    public Binding productCreatedBinding(
            Queue inventoryProductCreatedQueue,
            TopicExchange primeCartExchange) {

        return BindingBuilder
                .bind(inventoryProductCreatedQueue)
                .to(primeCartExchange)
                .with(
                        RabbitMqConstants
                                .PRODUCT_CREATED_ROUTING_KEY
                );
    }

    @Bean
    public Binding productCreatedDlqBinding(
            Queue inventoryProductCreatedDlq,
            DirectExchange deadLetterExchange) {

        return BindingBuilder
                .bind(inventoryProductCreatedDlq)
                .to(deadLetterExchange)
                .with(
                        RabbitMqConstants
                                .INVENTORY_PRODUCT_CREATED_DLQ
                );
    }
}
```

---

# Consumer Retry Configuration

Add to Inventory Service:

```yaml
spring:
  rabbitmq:
    listener:
      simple:
        acknowledge-mode: auto
        default-requeue-rejected: false

        retry:
          enabled: true
          max-attempts: 3
          initial-interval: 1s
          multiplier: 2
          max-interval: 10s
```

Retry flow:

```text
Attempt 1
   |
   | failure
   v
Attempt 2
   |
   | failure
   v
Attempt 3
   |
   | failure
   v
Dead Letter Queue
```

`default-requeue-rejected: false` prevents permanently failing messages from being requeued forever.

---

# Event Validation

Validate required fields before creating inventory:

```java
private void validate(ProductCreatedEvent event) {

    if (event.eventId() == null) {
        throw new IllegalArgumentException(
                "eventId is required"
        );
    }

    if (event.productId() == null) {
        throw new IllegalArgumentException(
                "productId is required"
        );
    }

    if (event.sku() == null ||
        event.sku().isBlank()) {

        throw new IllegalArgumentException(
                "sku is required"
        );
    }
}
```

Invalid events fail, retry, and eventually reach the DLQ.

---

# Consumer Idempotency

RabbitMQ can redeliver messages.

The consumer must prevent duplicate inventory creation.

## Basic Idempotency

Use a unique constraint:

```java
@Table(
        name = "inventory",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inventory_product_id",
                        columnNames = "product_id"
                )
        }
)
```

Repository method:

```java
boolean existsByProductId(Long productId);
```

Consumer check:

```java
if (inventoryRepository.existsByProductId(
        event.productId())) {

    log.info(
            "Duplicate ProductCreatedEvent ignored. " +
            "eventId={}, productId={}",
            event.eventId(),
            event.productId()
    );

    return;
}
```

## Stronger Idempotency

Create a processed event table:

```text
processed_events
```

Suggested columns:

```text
event_id
event_type
processed_at
```

Add a unique constraint on `event_id`.

Processing flow:

```text
Receive Event
    |
Check event_id
    |
Already exists?
    |
Yes --> Ignore duplicate
No  --> Process and save event_id
```

---

# Inventory Consumer

```java
package com.primecart.messaging.consumer;

import com.primecart.entity.Inventory;
import com.primecart.messaging.RabbitMqConstants;
import com.primecart.messaging.events.ProductCreatedEvent;
import com.primecart.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCreatedEventConsumer {

    private final InventoryRepository inventoryRepository;

    @Transactional
    @RabbitListener(
            queues =
                    RabbitMqConstants
                            .INVENTORY_PRODUCT_CREATED_QUEUE
    )
    public void consume(ProductCreatedEvent event) {

        log.info(
                "Received ProductCreatedEvent. " +
                "eventId={}, productId={}",
                event.eventId(),
                event.productId()
        );

        validate(event);

        if (inventoryRepository.existsByProductId(
                event.productId())) {

            log.info(
                    "Duplicate event ignored. " +
                    "eventId={}, productId={}",
                    event.eventId(),
                    event.productId()
            );

            return;
        }

        Inventory inventory =
                Inventory.builder()
                        .productId(event.productId())
                        .sku(event.sku())
                        .availableQuantity(
                                event.initialStock() == null
                                        ? 0
                                        : event.initialStock()
                        )
                        .reservedQuantity(0)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        inventoryRepository.save(inventory);

        log.info(
                "Inventory created asynchronously. " +
                "eventId={}, productId={}",
                event.eventId(),
                event.productId()
        );
    }

    private void validate(ProductCreatedEvent event) {

        if (event.eventId() == null) {
            throw new IllegalArgumentException(
                    "eventId is required"
            );
        }

        if (event.productId() == null) {
            throw new IllegalArgumentException(
                    "productId is required"
            );
        }

        if (event.sku() == null ||
            event.sku().isBlank()) {

            throw new IllegalArgumentException(
                    "sku is required"
            );
        }
    }
}
```

---

# DLQ Consumer

A DLQ consumer can be added for monitoring or controlled replay.

```java
package com.primecart.messaging.consumer;

import com.primecart.messaging.RabbitMqConstants;
import com.primecart.messaging.events.ProductCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductCreatedDlqConsumer {

    @RabbitListener(
            queues =
                    RabbitMqConstants
                            .INVENTORY_PRODUCT_CREATED_DLQ
    )
    public void consumeDlq(ProductCreatedEvent event) {

        log.error(
                "Message received from DLQ. " +
                "eventId={}, productId={}",
                event.eventId(),
                event.productId()
        );
    }
}
```

For production, do not automatically republish every DLQ message without investigating the cause.

---

# RabbitMQ Metrics

## Metrics to Track

- Published events
- Successfully consumed events
- Failed consumption attempts
- Duplicate events
- DLQ messages
- Queue depth

## Example Custom Metrics

```java
package com.primecart.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqMetrics {

    private final Counter productEventsPublished;
    private final Counter productEventsConsumed;
    private final Counter productEventFailures;
    private final Counter duplicateProductEvents;
    private final Counter productEventsDlq;

    public RabbitMqMetrics(
            MeterRegistry meterRegistry) {

        this.productEventsPublished =
                Counter.builder(
                        "primecart.rabbitmq.product.events.published"
                )
                .description(
                        "Total ProductCreatedEvent messages published"
                )
                .register(meterRegistry);

        this.productEventsConsumed =
                Counter.builder(
                        "primecart.rabbitmq.product.events.consumed"
                )
                .description(
                        "Total ProductCreatedEvent messages consumed"
                )
                .register(meterRegistry);

        this.productEventFailures =
                Counter.builder(
                        "primecart.rabbitmq.product.events.failed"
                )
                .description(
                        "Total ProductCreatedEvent processing failures"
                )
                .register(meterRegistry);

        this.duplicateProductEvents =
                Counter.builder(
                        "primecart.rabbitmq.product.events.duplicate"
                )
                .description(
                        "Total duplicate ProductCreatedEvent messages"
                )
                .register(meterRegistry);

        this.productEventsDlq =
                Counter.builder(
                        "primecart.rabbitmq.product.events.dlq"
                )
                .description(
                        "Total ProductCreatedEvent messages consumed from DLQ"
                )
                .register(meterRegistry);
    }

    public void incrementPublished() {
        productEventsPublished.increment();
    }

    public void incrementConsumed() {
        productEventsConsumed.increment();
    }

    public void incrementFailed() {
        productEventFailures.increment();
    }

    public void incrementDuplicate() {
        duplicateProductEvents.increment();
    }

    public void incrementDlq() {
        productEventsDlq.increment();
    }
}
```

Prometheus names:

```text
primecart_rabbitmq_product_events_published_total
primecart_rabbitmq_product_events_consumed_total
primecart_rabbitmq_product_events_failed_total
primecart_rabbitmq_product_events_duplicate_total
primecart_rabbitmq_product_events_dlq_total
```

---

# RabbitMQ Queue Metrics

Queue-level metrics should be collected using the RabbitMQ Prometheus plugin.

Important metrics:

- Queue depth
- Ready messages
- Unacknowledged messages
- Publish rate
- Deliver rate
- Consumer count
- Connection count
- Channel count

RabbitMQ Prometheus endpoint:

```text
http://localhost:15692/metrics
```

Example RabbitMQ image configuration may require enabling the Prometheus plugin.

Useful Grafana panels:

- Queue depth
- Ready messages
- Unacknowledged messages
- Consumer count
- Publish rate
- Delivery rate
- DLQ depth

---

# Trace and Correlation Information

Add useful metadata to messages:

```java
rabbitTemplate.convertAndSend(
        RabbitMqConstants.PRIME_CART_EXCHANGE,
        RabbitMqConstants.PRODUCT_CREATED_ROUTING_KEY,
        event,
        message -> {

            message.getMessageProperties()
                    .setHeader(
                            "eventId",
                            event.eventId().toString()
                    );

            message.getMessageProperties()
                    .setHeader(
                            "eventType",
                            event.eventType()
                    );

            return message;
        },
        correlationData
);
```

Log these fields:

```text
eventId
eventType
productId
exchange
routingKey
queue
```

This helps trace:

```text
POST /api/products
      |
Product saved
      |
product.created published
      |
Inventory event consumed
      |
Inventory row created
```

---

# Testing Scenarios

## 1. Normal Flow

1. Start RabbitMQ.
2. Start Inventory Service.
3. Start Product Service.
4. Create a product.
5. Verify event publication.
6. Verify inventory row creation.

Expected Product Service logs:

```text
Product created with id: 7
Database transaction committed
Publishing ProductCreatedEvent
RabbitMQ confirmed message
```

Expected Inventory Service logs:

```text
Received ProductCreatedEvent
Inventory created asynchronously
```

---

## 2. Consumer Downtime Test

1. Stop Inventory Service.
2. Create three products.
3. Product Service should still succeed.
4. Open RabbitMQ Management UI.
5. Check ready messages in:

```text
inventory.product-created.queue
```

6. Restart Inventory Service.
7. Verify all queued events are consumed.
8. Verify inventory rows are created.

This demonstrates producer-consumer decoupling.

---

## 3. Retry Test

Temporarily throw an exception:

```java
if ("FAIL-RETRY-TEST".equals(event.sku())) {
    throw new IllegalStateException(
            "Simulated consumer failure"
    );
}
```

Expected:

```text
Attempt 1 failed
Attempt 2 failed
Attempt 3 failed
Message moved to DLQ
```

---

## 4. DLQ Test

Publish an invalid event or force the consumer to fail.

Verify:

```text
inventory.product-created.dlq
```

contains the message.

---

## 5. Duplicate Event Test

Publish the same event twice.

Expected:

```text
First event:
Inventory created

Second event:
Duplicate event ignored
```

Verify only one inventory row exists.

---

## 6. Unroutable Message Test

Publish with the wrong routing key:

```text
product.invalid
```

Expected Product Service log:

```text
RabbitMQ returned unroutable message
replyText=NO_ROUTE
```

---

# Common Problems

## NO_ROUTE

Example:

```text
RabbitMQ returned unroutable message
exchange=primecart.events
routingKey=product.created
replyText=NO_ROUTE
```

Check:

- Queue exists
- Binding exists
- Routing key matches exactly
- Same virtual host is used
- Inventory Service successfully started
- Exchange type matches

---

## PRECONDITION_FAILED

Occurs when an existing exchange or queue has different properties.

Example:

```text
Exchange already exists as direct
Application declares topic
```

For local development:

1. Delete the conflicting exchange or queue.
2. Restart Inventory Service.
3. Verify topology.

---

## Infinite Redelivery

Cause:

```yaml
default-requeue-rejected: true
```

Fix:

```yaml
default-requeue-rejected: false
```

Use retry and DLQ instead.

---

## Duplicate Inventory Rows

Fix:

- Unique constraint on `product_id`
- Duplicate check
- Processed event table

---

## Message Published but Inventory Missing

Possible causes:

- Consumer stopped
- Message waiting in queue
- Consumer failed and message reached DLQ
- Event validation failed
- Database transaction failed

Check RabbitMQ queue depth and Inventory Service logs.

---

# Service Data Ownership

Recommended ownership:

## Product Service

- Product name
- Description
- Price
- SKU
- Category
- Brand
- Active status

## Inventory Service

- Available quantity
- Reserved quantity
- Inventory status

Avoid maintaining independent stock values in both Product Service and Inventory Service.

A cleaner event can initialize inventory with zero:

```json
{
  "productId": 7,
  "sku": "LAPTOP-007",
  "initialStock": 0
}
```

Inventory Service should remain the final owner of stock quantity.

---

# Reliability Limitations

## Current Database and Message Gap

Current flow:

```text
Save Product
      |
Commit Product DB
      |
Publish Event
```

Failure scenario:

```text
Product DB committed
Application crashes
Event not published
```

Result:

```text
Product exists
Inventory row does not exist
```

Publisher confirms do not solve this because the application may fail before publishing.

---

# Transactional Outbox Pattern

Recommended production solution:

```text
Single Product DB Transaction
      |
      +-- Insert Product
      |
      +-- Insert Outbox Event
      |
      v
Commit
```

Then:

```text
Outbox Publisher
      |
Read unpublished events
      |
Publish to RabbitMQ
      |
Mark event as published
```

Benefits:

- Product and outbox event commit together
- Events are not silently lost
- Failed publishing can be retried
- Better auditability

Suggested outbox table:

```text
outbox_events
```

Suggested columns:

```text
id
event_id
aggregate_type
aggregate_id
event_type
payload
status
created_at
published_at
retry_count
```

---

# Future Events

After `product.created` becomes reliable, add:

```text
product.updated
product.deleted
```

Potential consumers:

- Inventory Service
- Search Service
- Audit Service
- Notification Service

---

# Future Saga Roadmap

Saga will be introduced later for:

```text
Order Service
      |
      | order.created
      v
Inventory Service
      |
      | inventory.reserved
      v
Payment Service
      |
      | payment.completed
      v
Order Service
      |
      v
Order Confirmed
```

Failure compensation:

```text
payment.failed
      |
      +--> Inventory Service releases stock
      |
      +--> Order Service marks order failed
```

This will introduce:

- Distributed transaction handling
- Eventual consistency
- Compensating transactions
- Saga choreography

---

# Recommended Implementation Order

1. Basic `product.created` event
2. Durable exchange and queue
3. Publisher confirms
4. Mandatory returned messages
5. Event validation
6. Consumer retry
7. Dead Letter Queue
8. Consumer idempotency
9. RabbitMQ metrics
10. Trace propagation
11. Downtime testing
12. Product update event
13. Product delete event
14. Transactional Outbox Pattern
15. Order–Inventory–Payment Saga

---

# Interview Explanation

> In PrimeCart, I introduced RabbitMQ using a simple asynchronous flow between Product Service and Inventory Service. When a product is created, Product Service publishes a `product.created` event to a durable topic exchange. Inventory Service consumes the event and creates its local inventory record. I added publisher confirms, mandatory returns for unroutable messages, consumer retry, dead-letter handling, event validation, and idempotent processing. I also exposed custom Micrometer metrics and RabbitMQ queue metrics to Prometheus and Grafana. The current design uses eventual consistency. The next reliability enhancement is the Transactional Outbox Pattern, followed by a Saga-based Order–Inventory–Payment workflow.

---

# Final Checklist

- [x] RabbitMQ Docker container added
- [x] Management UI enabled
- [x] Spring AMQP dependency added
- [x] Topic exchange created
- [x] Routing key configured
- [x] Durable queue created
- [x] Queue binding created
- [x] JSON message converter added
- [x] Product event publisher added
- [x] Event published after database commit
- [x] Publisher confirms enabled
- [x] Mandatory returned messages enabled
- [x] Inventory consumer added
- [x] Event validation added
- [x] Retry configured
- [x] Dead Letter Exchange configured
- [x] Dead Letter Queue configured
- [x] Duplicate event handling added
- [x] RabbitMQ custom metrics planned or implemented
- [x] Queue monitoring planned
- [x] Downtime test completed
- [ ] Processed event table
- [ ] Transactional Outbox Pattern
- [ ] Product updated event
- [ ] Product deleted event
- [ ] Saga pattern
