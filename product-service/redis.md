# Redis Caching Features - PrimeCart Product Service

## Overview

The Product Service uses **Spring Cache + Redis** to improve application performance by reducing database queries for
frequently accessed product data.

The implementation follows production-ready caching practices including cache invalidation, TTL configuration, JSON
serialization, monitoring, and fault tolerance.

---

# Implemented Features

## 1. JSON Serialization ✅

### Description

Configured Redis to store cached objects as **JSON** instead of Java binary serialization.

### Benefits

- Human-readable data in RedisInsight
- Easier debugging
- Better interoperability
- Avoids Java Serialization issues

### Configuration

- `GenericJackson2JsonRedisSerializer`
- `ObjectMapper`
- `JavaTimeModule`

---

## 2. Separate TTL per Cache ✅

Different caches have different expiration times based on usage patterns.

| Cache         | TTL        | Purpose            |
|---------------|------------|--------------------|
| `products`    | 30 Minutes | Individual Product |
| `allProducts` | 10 Minutes | Product Listing    |

### Why?

Individual products change less frequently than product listings.

---

## 3. Disable Null Value Caching ✅

Configured Redis to never cache null values.

```java
.disableCachingNullValues()
```

### Benefits

- Prevents unnecessary cache entries
- Saves Redis memory
- Avoids caching failed lookups

Without this:

```
products::100

value = null
```

---

## 4. Cache Key Prefix ✅

Configured Redis cache prefix.

```java
.prefixCacheNameWith("primecart::")
```

### Cache Keys

Before

```
products::1
```

After

```
primecart::products::1
primecart::allProducts::SimpleKey[]
```

### Benefits

- Prevents key collisions
- Useful when multiple applications share Redis
- Easier key organization

---

## 5. Transaction-Aware Cache Manager ✅

Configured cache manager with transaction awareness.

```java
.transactionAware()
```

### Benefits

If a database transaction rolls back, cache changes are also rolled back, preventing stale or inconsistent cache
entries.

---

## 6. Redis Connection Pool (Lettuce) ✅

Configured Redis connection pooling.

```yaml
spring:
  data:
    redis:
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 2
          max-wait: 2s
```

### Benefits

- Better throughput
- Efficient connection reuse
- Supports concurrent requests

---

## 7. Cache Statistics & Monitoring ✅

Enabled Spring Boot Actuator cache endpoints.

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,caches
```

### Metrics

- Cache Hits
- Cache Misses
- Cache Evictions

### Monitoring Stack

```
Spring Boot
      │
Micrometer
      │
Prometheus
      │
Grafana
```

---

## 8. Redis Health Check ✅

Enabled Redis health monitoring.

Endpoint

```
GET /actuator/health
```

Example Response

```json
{
  "status": "UP",
  "components": {
    "redis": {
      "status": "UP"
    }
  }
}
```

### Benefits

- Health monitoring
- Production readiness
- Kubernetes readiness/liveness checks

---

## 9. Cache Names Constants ✅

Instead of hardcoding cache names:

```java
@Cacheable("products")
```

Created constants.

```java
public final class CacheNames {

    public static final String PRODUCTS = "products";
    public static final String ALL_PRODUCTS = "allProducts";

    private CacheNames() {
    }
}
```

Usage

```java
@Cacheable(CacheNames.PRODUCTS)
```

### Benefits

- Eliminates typos
- Centralized cache names
- Easier maintenance

---

## 10. Cache Error Handler ✅

Implemented custom `CacheErrorHandler`.

### Scenario

If Redis becomes unavailable:

```
Application
      │
Redis Down
      │
Database
```

Instead of returning

```
500 Internal Server Error
```

The application

- Logs the Redis error
- Continues serving data from the database

### Benefits

- Improved availability
- Graceful degradation
- Better user experience

---

## 11. Redis Logging (Development) ✅

Enabled Redis debug logging.

```yaml
logging:
  level:
    org.springframework.data.redis: DEBUG
```

### Benefits

- View cache hits
- View cache misses
- Debug cache operations

---

## 12. Cache Invalidation Strategy ✅

### Product Details Cache

```
products
```

Cached using

```java
@Cacheable("products")
```

Evicted on

- Update Product
- Delete Product

---

### Product List Cache

```
allProducts
```

Cached using

```java
@Cacheable("allProducts")
```

Evicted on

- Create Product
- Update Product
- Delete Product

using

```java
@CacheEvict(value = "allProducts", allEntries = true)
```

### Why?

Whenever product data changes, all cached product listings may become stale.

Instead of trying to update every cached page/filter combination, the entire list cache is cleared.

The next request automatically repopulates the cache with fresh data.

---

## 13. Cache Hit Ratio Monitoring ✅

The application exposes cache metrics through Micrometer.

Metrics can be visualized in Grafana.

Typical metrics include

- Cache Hit Ratio
- Cache Miss Ratio
- Cache Evictions
- Cache Size

### Example KPI

```
Cache Hit Ratio = 90%

Database Queries Reduced = High

Application Response Time = Low
```

---

# Architecture

```
                    Client
                       │
                       ▼
              Product Controller
                       │
                       ▼
              Spring Cache Layer
               │             │
        Cache Hit      Cache Miss
               │             │
               ▼             ▼
             Redis      Product Service
                              │
                              ▼
                         MySQL Database
                              │
                              ▼
                           Redis Cache
```

---

# Cache Lifecycle

```
GET /products/1

        │
        ▼

Redis Cache?

   Yes ─────────► Return Cached Data

   No
        │
        ▼

Database Query

        │
        ▼

Store Result in Redis

        │
        ▼

Return Response
```

---

# Cache Eviction Flow

```
Update Product

      │
      ▼

Database Updated

      │
      ▼

Evict

products::id

allProducts::*

      │
      ▼

Next Request

      │
      ▼

Fresh Data Loaded

      │
      ▼

Stored Back into Redis
```

---

# Production Benefits

- Faster API response time
- Reduced database load
- Lower latency
- Improved scalability
- Better fault tolerance
- Readable JSON cache entries
- Automatic cache expiration
- Transaction-safe cache updates
- Redis health monitoring
- Cache metrics with Prometheus & Grafana
- Graceful fallback when Redis is unavailable
