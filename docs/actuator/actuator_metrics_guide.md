# PrimeCart - Important Spring Boot Actuator Metrics

## Overview

Spring Boot Actuator provides production-ready metrics that help monitor
the health, performance, and stability of microservices. Combined with
Prometheus and Grafana, these metrics provide complete observability.

------------------------------------------------------------------------

# 1. JVM Metrics ⭐⭐⭐⭐⭐

**Metrics** - `jvm.memory.used` - `jvm.memory.max` - `jvm.gc.pause` -
`jvm.threads.live`

**Purpose** - Detect memory leaks - Monitor garbage collection - Track
thread usage - Prevent OutOfMemoryError

**Example**

    Used Memory : 650 MB
    Max Memory  : 1024 MB

------------------------------------------------------------------------

# 2. HTTP Request Metrics ⭐⭐⭐⭐⭐

**Metric** - `http.server.requests`

**Provides** - Request count - Average response time - Maximum response
time - Status codes - URI - HTTP method

**Example Dashboard**

    GET /api/products

    Count        : 15,234
    Average Time : 18 ms
    95th Percentile : 65 ms
    Max          : 420 ms

------------------------------------------------------------------------

# 3. HikariCP Connection Pool ⭐⭐⭐⭐⭐

**Metrics** - `hikaricp.connections.active` -
`hikaricp.connections.idle` - `hikaricp.connections.pending` -
`hikaricp.connections.max`

**Purpose** - Detect connection leaks - Identify pool exhaustion -
Monitor database utilization

------------------------------------------------------------------------

# 4. JDBC Metrics ⭐⭐⭐⭐☆

**Metrics** - `jdbc.connections.active` - `jdbc.connections.max`

Useful for understanding database connectivity and bottlenecks.

------------------------------------------------------------------------

# 5. CPU Metrics ⭐⭐⭐⭐⭐

**Metrics** - `system.cpu.usage` - `process.cpu.usage`

**Purpose** - Detect high CPU usage - Identify infinite loops - Support
scaling decisions

------------------------------------------------------------------------

# 6. Disk Space ⭐⭐⭐⭐⭐

Health endpoint exposes:

    /actuator/health

Useful for monitoring available disk space and preventing failures
caused by full disks.

------------------------------------------------------------------------

# 7. Redis Metrics ⭐⭐⭐⭐☆

**Examples** - `redis.commands`

Useful for monitoring cache operations and Redis performance.

------------------------------------------------------------------------

# 8. Cache Metrics ⭐⭐⭐⭐⭐

**Metrics** - `cache.gets` - `cache.puts` - `cache.evictions`

Useful for evaluating cache effectiveness.

Example KPI:

    Cache Hit Ratio = 95%

------------------------------------------------------------------------

# 9. Thread Pool Metrics ⭐⭐⭐⭐☆

For applications using `@Async`.

**Metrics** - `executor.active` - `executor.completed` -
`executor.queued`

Useful for detecting thread starvation and queue buildup.

------------------------------------------------------------------------

# 10. Process Metrics ⭐⭐⭐⭐☆

**Metrics** - `process.uptime` - `process.start.time`

Useful for identifying unexpected service restarts.

------------------------------------------------------------------------

# 11. Logback Metrics ⭐⭐⭐☆☆

**Metric** - `logback.events`

Tracks the number of: - INFO logs - WARN logs - ERROR logs

Helpful for detecting spikes in application errors.

------------------------------------------------------------------------

# 12. Custom Business Metrics ⭐⭐⭐⭐⭐

Recommended custom Micrometer counters for PrimeCart:

    primecart.product.created
    primecart.product.updated
    primecart.product.deleted
    primecart.product.viewed

    primecart.orders.created
    primecart.orders.failed

    primecart.payments.success
    primecart.payments.failed

    primecart.cart.checkedout

    primecart.inventory.reserved
    primecart.inventory.out_of_stock

These metrics are ideal for Grafana dashboards.

------------------------------------------------------------------------

# 13. Resilience4j Metrics ⭐⭐⭐⭐⭐

If using Resilience4j:

    resilience4j.circuitbreaker.calls
    resilience4j.retry.calls
    resilience4j.bulkhead.calls
    resilience4j.ratelimiter.available.permissions

Useful for monitoring retries, circuit breaker state, rate limiting, and
bulkhead usage.

------------------------------------------------------------------------

# 14. Messaging Metrics ⭐⭐⭐☆☆

If RabbitMQ or Kafka is introduced later:

-   Messages Published
-   Messages Consumed
-   Consumer Lag

Useful for monitoring asynchronous communication.

------------------------------------------------------------------------

# Recommended Metrics for PrimeCart

  Category                   Priority
  -------------------------- ------------
  HTTP Request Metrics       ⭐⭐⭐⭐⭐
  JVM Memory & GC            ⭐⭐⭐⭐⭐
  CPU Usage                  ⭐⭐⭐⭐⭐
  HikariCP Connection Pool   ⭐⭐⭐⭐⭐
  Cache Metrics              ⭐⭐⭐⭐⭐
  Resilience4j Metrics       ⭐⭐⭐⭐⭐
  Custom Business Metrics    ⭐⭐⭐⭐⭐
  Redis Metrics              ⭐⭐⭐⭐☆
  Thread Pool Metrics        ⭐⭐⭐⭐☆
  Disk Space                 ⭐⭐⭐⭐☆
  Logback Metrics            ⭐⭐⭐☆☆
  Messaging Metrics          ⭐⭐⭐☆☆

------------------------------------------------------------------------

# Interview Talking Points

When diagnosing a production issue:

1.  Check `http.server.requests` for latency and error rates.
2.  Review HikariCP metrics for database connection exhaustion.
3.  Examine JVM memory and GC metrics for memory pressure.
4.  Monitor CPU utilization.
5.  Verify cache hit ratio.
6.  Review Resilience4j metrics for retries and circuit breaker
    activity.
7.  Inspect custom business metrics to understand business impact.

This demonstrates practical experience with observability and production
monitoring.
