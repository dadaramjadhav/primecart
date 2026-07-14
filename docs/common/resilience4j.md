# Resilience4j Implementation in PrimeCart

## Overview

Resilience4j has been implemented in the **Order Service** around its synchronous call to the **Inventory Service**.

```text
Order Service
     |
     | Retry
     | Circuit Breaker
     | Timeout
     | Bulkhead
     | Fallback
     v
Inventory Service
```

The purpose of this implementation is to prevent failures or slowness in Inventory Service from exhausting Order Service resources or causing uncontrolled failures.

---

## Services Used

| Service | Port |
|---|---:|
| Order Service | 8082 |
| Inventory Service | 8084 |

The protected communication flow is:

```text
POST /api/orders
      |
      v
Order Service
      |
      v
Inventory Service
```

---

# Patterns Implemented

## 1. Retry

Retry is used for temporary downstream failures.

Examples:

- Connection refused
- Connection reset
- Read timeout
- Temporary HTTP 502
- Temporary HTTP 503
- Temporary HTTP 504

Retry should not be used for business failures such as:

- Invalid request
- Product not found
- Insufficient inventory
- Authentication failure
- Authorization failure

Recommended configuration:

```yaml
resilience4j:
  retry:
    instances:
      inventoryService:
        max-attempts: 3
        wait-duration: 1s
        enable-exponential-backoff: true
        exponential-backoff-multiplier: 2
        retry-exceptions:
          - feign.RetryableException
          - java.net.SocketTimeoutException
          - java.io.IOException
```

Example behavior:

```text
Attempt 1: immediately
Attempt 2: after 1 second
Attempt 3: after 2 seconds
```

---

## 2. Circuit Breaker

Circuit Breaker stops Order Service from repeatedly calling Inventory Service when the downstream service is unhealthy.

### States

```text
CLOSED
   |
   | Failure threshold reached
   v
OPEN
   |
   | Wait duration completed
   v
HALF_OPEN
   |
   +-- Successful trial calls --> CLOSED
   |
   `-- Failed trial calls -----> OPEN
```

Configuration:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      inventoryService:
        register-health-indicator: true
        sliding-window-type: COUNT_BASED
        sliding-window-size: 10
        minimum-number-of-calls: 5
        failure-rate-threshold: 50
        permitted-number-of-calls-in-half-open-state: 3
        wait-duration-in-open-state: 15s
        automatic-transition-from-open-to-half-open-enabled: true
```

Meaning:

- Track the latest 10 calls.
- Start calculating failures after at least 5 calls.
- Open the circuit when 50% or more calls fail.
- Keep the circuit open for 15 seconds.
- Allow 3 test calls in the half-open state.

---

## 3. Timeout

Feign connection and read timeouts protect Order Service from waiting indefinitely for Inventory Service.

```yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          inventory-service:
            connect-timeout: 1000
            read-timeout: 2000
```

Meaning:

- Connection timeout: 1 second
- Read timeout: 2 seconds

For the current synchronous Feign call, Feign HTTP timeout is the primary timeout protection.

---

## 4. Bulkhead

Bulkhead limits the number of concurrent calls from Order Service to Inventory Service.

```yaml
resilience4j:
  bulkhead:
    instances:
      inventoryService:
        max-concurrent-calls: 5
        max-wait-duration: 0
```

Meaning:

- Maximum 5 concurrent Inventory Service calls.
- Additional calls are rejected immediately.
- Rejected calls are handled by fallback.

For local testing, use:

```yaml
max-concurrent-calls: 2
```

This makes bulkhead rejection easy to reproduce using concurrent requests.

---

## 5. Fallback

Fallback provides controlled behavior when:

- Retry attempts are exhausted
- Circuit Breaker is open
- Timeout occurs
- Bulkhead is full
- Inventory Service is unavailable

Example for the current `void` reservation operation:

```text
private void reserveStockFallback(
        ReserveStockRequest request,
        Throwable throwable) {

    log.error(
            "Inventory reservation failed. productId={}, quantity={}, reason={}",
            request.getProductId(),
            request.getQuantity(),
            throwable.getMessage(),
            throwable
    );

    throw new InventoryReservationException(
            "Inventory Service is temporarily unavailable. "
                    + "Unable to reserve inventory for product "
                    + request.getProductId()
    );
}
```

The fallback method must:

- Have the same input parameters as the protected method
- Add `Throwable` or a supported exception as the last parameter
- Return the same type as the protected method

---

# Maven Dependencies

Added to Order Service:

```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

OpenFeign dependency:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

---

# Integration Service

A separate Spring bean wraps the Inventory Feign client.

This avoids the Spring AOP self-invocation problem.

```java
package com.primecart.service;

import com.primecart.client.InventoryClient;
import com.primecart.dto.request.ReserveStockRequest;
import com.primecart.exception.InventoryReservationException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryIntegrationService {

    private static final String INVENTORY_SERVICE = "inventoryService";

    private final InventoryClient inventoryClient;

    @Retry(
            name = INVENTORY_SERVICE,
            fallbackMethod = "reserveStockFallback"
    )
    @CircuitBreaker(
            name = INVENTORY_SERVICE,
            fallbackMethod = "reserveStockFallback"
    )
    @Bulkhead(
            name = INVENTORY_SERVICE,
            type = Bulkhead.Type.SEMAPHORE,
            fallbackMethod = "reserveStockFallback"
    )
    public void reserveStock(ReserveStockRequest request) {

        log.info(
                "Calling Inventory Service to reserve stock. productId={}, quantity={}",
                request.getProductId(),
                request.getQuantity()
        );

        inventoryClient.reserveStock(request);
    }

    private void reserveStockFallback(
            ReserveStockRequest request,
            Throwable throwable) {

        log.error(
                "Inventory reservation failed. productId={}, quantity={}, reason={}",
                request.getProductId(),
                request.getQuantity(),
                throwable.getMessage(),
                throwable
        );

        throw new InventoryReservationException(
                "Inventory Service is temporarily unavailable. "
                        + "Unable to reserve inventory for product "
                        + request.getProductId()
        );
    }
}
```

---

# Why a Separate Integration Service Was Used

The following approach does not reliably activate Resilience4j:

```java
@Service
public class OrderServiceImpl {

    @Retry(name = "inventoryService")
    public void reserveStock() {
    }

    public void createOrder() {
        reserveStock();
    }
}
```

The call happens inside the same object and bypasses the Spring proxy.

Correct flow:

```text
OrderServiceImpl
      |
      v
InventoryIntegrationService
      |
      v
InventoryClient
```

---

# Combined Configuration

```yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          inventory-service:
            connect-timeout: 1000
            read-timeout: 2000
            logger-level: basic

resilience4j:

  retry:
    instances:
      inventoryService:
        max-attempts: 3
        wait-duration: 1s
        enable-exponential-backoff: true
        exponential-backoff-multiplier: 2
        retry-exceptions:
          - feign.RetryableException
          - java.net.SocketTimeoutException
          - java.io.IOException

  circuitbreaker:
    instances:
      inventoryService:
        register-health-indicator: true
        sliding-window-type: COUNT_BASED
        sliding-window-size: 10
        minimum-number-of-calls: 5
        failure-rate-threshold: 50
        permitted-number-of-calls-in-half-open-state: 3
        wait-duration-in-open-state: 15s
        automatic-transition-from-open-to-half-open-enabled: true

  bulkhead:
    instances:
      inventoryService:
        max-concurrent-calls: 5
        max-wait-duration: 0

management:
  endpoints:
    web:
      exposure:
        include:
          - health
          - info
          - metrics
          - prometheus
          - circuitbreakers
          - circuitbreakerevents
          - retries
          - retryevents
          - bulkheads
          - bulkheadevents

  endpoint:
    health:
      show-details: always

  health:
    circuitbreakers:
      enabled: true
```

---

# Testing Retry

## Method 1: Stop Inventory Service

Stop Inventory Service and call Order Service.

```bash
curl -X POST http://localhost:8082/api/orders \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d @order.json
```

Expected result:

- Order Service attempts the Inventory Service call.
- Retry executes according to the configured maximum attempts.
- Fallback executes after retries are exhausted.

---

## Method 2: Temporary Failure

Make Inventory Service fail for the first two requests and succeed on the third request.

Expected logs:

```text
Inventory reservation attempt: 1
Inventory reservation attempt: 2
Inventory reservation attempt: 3
Inventory reserved successfully
```

This verifies that Retry is working.

---

# Testing Timeout

Add a temporary delay in Inventory Service:

```text
Thread.sleep(5000);
```

Configured read timeout:

```yaml
read-timeout: 2000
```

Expected behavior:

- Inventory Service takes 5 seconds.
- Order Service times out after approximately 2 seconds.
- Retry may attempt the request again.
- Fallback executes after attempts are exhausted.

Important:

```text
Total API duration =
number of attempts × timeout
+ retry wait durations
```

Use small retry and timeout values to avoid long user-facing requests.

---

# Testing Circuit Breaker

1. Stop Inventory Service.
2. Send at least five Order Service requests.
3. Observe failures.
4. Circuit Breaker moves from `CLOSED` to `OPEN`.
5. New calls fail immediately without reaching Inventory Service.
6. Restart Inventory Service.
7. Wait 15 seconds.
8. Circuit Breaker moves to `HALF_OPEN`.
9. Successful trial calls move it back to `CLOSED`.

Check health:

```bash
curl http://localhost:8082/actuator/health
```

Check events:

```bash
curl http://localhost:8082/actuator/circuitbreakerevents
```

---

# Testing Bulkhead

Set:

```yaml
max-concurrent-calls: 2
```

Make Inventory Service slow:

```text
Thread.sleep(5000);
```

Send concurrent requests using ApacheBench:

```bash
ab \
  -n 20 \
  -c 10 \
  -T application/json \
  -p order.json \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  http://localhost:8082/api/orders
```

Expected behavior:

- Only 2 calls are allowed concurrently.
- Remaining calls are rejected.
- Rejected calls produce `BulkheadFullException`.
- Fallback handles rejected calls.

---

# Testing All Patterns Together

Use this test setup:

```yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          inventory-service:
            connect-timeout: 1000
            read-timeout: 2000

resilience4j:
  retry:
    instances:
      inventoryService:
        max-attempts: 2
        wait-duration: 300ms

  circuitbreaker:
    instances:
      inventoryService:
        sliding-window-size: 10
        minimum-number-of-calls: 5
        failure-rate-threshold: 50
        wait-duration-in-open-state: 15s

  bulkhead:
    instances:
      inventoryService:
        max-concurrent-calls: 2
        max-wait-duration: 0
```

Test scenarios:

| Scenario | Expected Pattern |
|---|---|
| Inventory returns temporary 503 | Retry |
| Inventory is slow | Timeout |
| Inventory repeatedly fails | Circuit Breaker |
| Many concurrent slow requests | Bulkhead |
| All attempts fail | Fallback |

---

# Actuator Endpoints

Useful endpoints:

```text
/actuator/health
/actuator/circuitbreakers
/actuator/circuitbreakerevents
/actuator/retries
/actuator/retryevents
/actuator/bulkheads
/actuator/bulkheadevents
/actuator/prometheus
```

Examples:

```bash
curl http://localhost:8082/actuator/circuitbreakers
```

```bash
curl http://localhost:8082/actuator/circuitbreakerevents
```

```bash
curl http://localhost:8082/actuator/retryevents
```

---

# Prometheus Metrics

Search `/actuator/prometheus` for:

```text
resilience4j_circuitbreaker_state
resilience4j_circuitbreaker_calls_seconds
resilience4j_circuitbreaker_failure_rate
resilience4j_retry_calls_total
resilience4j_bulkhead_available_concurrent_calls
resilience4j_bulkhead_max_allowed_concurrent_calls
```

Example Prometheus query:

```promql
resilience4j_circuitbreaker_state{name="inventoryService",state="open"}
```

Retry calls:

```promql
sum by (name, kind) (
  resilience4j_retry_calls_total
)
```

Bulkhead available calls:

```promql
resilience4j_bulkhead_available_concurrent_calls{
  name="inventoryService"
}
```

---

# Grafana Dashboard Suggestions

Create panels for:

1. Circuit Breaker State
2. Circuit Breaker Failure Rate
3. Successful Calls
4. Failed Calls
5. Slow Calls
6. Retry Success Count
7. Retry Failure Count
8. Bulkhead Available Calls
9. Bulkhead Rejected Calls
10. Inventory Service Request Duration

---

# Important Production Considerations

## Retry Only Idempotent or Safe Operations

The inventory reservation operation may be invoked more than once because of Retry.

Therefore, Inventory Service must support idempotency.

Recommended approach:

```text
orderId + productId
```

Use this as a unique reservation key.

If the same reservation request is received again, return the existing reservation rather than reducing stock again.

---

## Avoid Retry Storms

Retry can increase traffic when a service is already unhealthy.

Use:

- Low retry count
- Short timeout
- Exponential backoff
- Circuit Breaker
- Jitter where supported
- Retry only for transient failures

---

## Do Not Retry Business Exceptions

Do not retry:

```text
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
Insufficient Stock
Invalid Product
Duplicate Reservation
```

---

## Configure Pattern Order Carefully

The combination of patterns affects behavior.

Conceptual flow:

```text
Bulkhead
   |
Circuit Breaker
   |
Retry
   |
Timeout
   |
Inventory Service
```

The effective annotation order is controlled by Resilience4j aspect order configuration. Test the actual behavior using logs and Actuator events rather than assuming the nesting.

---

## Use a Transaction Carefully

Do not keep a database transaction open while waiting through long remote retries.

A better flow is:

1. Validate request.
2. Save order as `PENDING`.
3. Commit the local transaction.
4. Call Inventory Service.
5. Update the final order status in a new transaction.

For a later event-driven implementation, RabbitMQ and the Saga pattern can replace the synchronous distributed workflow.

---

# Current Implementation Summary

The current PrimeCart implementation demonstrates:

- Resilient synchronous service-to-service communication
- OpenFeign timeout configuration
- Retry for transient failures
- Circuit Breaker for repeated failures
- Semaphore Bulkhead for concurrency isolation
- Controlled fallback behavior
- Actuator endpoints for runtime inspection
- Prometheus metrics for monitoring
- Grafana dashboard readiness

---

# Interview Explanation

> In PrimeCart, I implemented Resilience4j in Order Service around its OpenFeign call to Inventory Service. I configured short connect and read timeouts, retries only for transient failures, a circuit breaker to stop calls when Inventory Service is unhealthy, and a semaphore bulkhead to limit concurrent downstream calls. I used a separate integration bean so Spring AOP proxies could apply the Resilience4j annotations correctly. I also exposed circuit breaker, retry, and bulkhead metrics through Actuator and Prometheus for Grafana monitoring. Business failures such as insufficient stock are excluded from retries, and inventory reservation must be idempotent to safely handle repeated requests.

---

# Final Checklist

- [x] Resilience4j dependency added
- [x] Spring AOP dependency added
- [x] Actuator dependency available
- [x] Inventory Feign call wrapped in separate integration service
- [x] Retry configured
- [x] Circuit Breaker configured
- [x] Feign timeout configured
- [x] Bulkhead configured
- [x] Fallback method added
- [x] Actuator endpoints exposed
- [x] Prometheus metrics enabled
- [x] Retry test completed
- [x] Circuit Breaker test completed
- [x] Timeout test completed
- [x] Bulkhead test completed
