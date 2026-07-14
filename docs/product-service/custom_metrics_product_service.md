# Custom Metrics for Product Service

## Overview

This document describes custom Micrometer metrics implemented (or recommended) for the PrimeCart Product Service. These
metrics complement the default Spring Boot Actuator metrics and provide business and operational insights that can be
visualized in Prometheus and Grafana.

---

## Technology Stack

- Spring Boot 3.x
- Micrometer
- Prometheus
- Grafana
- Spring Boot Actuator

---

# Recommended Custom Metrics

| Metric                                | Type                | Purpose                             |
|---------------------------------------|---------------------|-------------------------------------|
| `primecart.product.created`           | Counter             | Total products created              |
| `primecart.product.updated`           | Counter             | Total products updated              |
| `primecart.product.deleted`           | Counter             | Total products deleted              |
| `primecart.product.viewed`            | Counter             | Total product retrieval requests    |
| `primecart.product.notfound`          | Counter             | Product not found requests          |
| `primecart.product.search`            | Counter             | Product search requests             |
| `primecart.cache.hit`                 | Counter             | Redis cache hits                    |
| `primecart.cache.miss`                | Counter             | Redis cache misses                  |
| `primecart.product.create.time`       | Timer               | Product creation latency            |
| `primecart.product.price`             | DistributionSummary | Product price distribution          |
| `primecart.products.total`            | Gauge               | Current total products              |
| `primecart.products.active`           | Gauge               | Current active products             |
| `primecart.products.lowstock`         | Gauge               | Products with stock below threshold |
| `primecart.products.outofstock`       | Gauge               | Products out of stock               |
| `primecart.product.validation.failed` | Counter             | Invalid product requests            |
| `primecart.product.duplicate.sku`     | Counter             | Duplicate SKU attempts              |

---

# Counter Metrics

## Product Created

**Metric**

```
primecart.product.created
```

**Purpose**

Counts every successful product creation.

---

## Product Updated

```
primecart.product.updated
```

Tracks successful product updates.

---

## Product Deleted

```
primecart.product.deleted
```

Tracks successful product deletions.

---

## Product Viewed

```
primecart.product.viewed
```

Counts successful product retrieval requests.

---

## Product Not Found

```
primecart.product.notfound
```

Increment whenever a requested product does not exist.

Useful for:

- Invalid requests
- Stale frontend cache
- Broken links
- API misuse

---

## Product Search

```
primecart.product.search
```

Tracks search operations.

---

## Cache Hit

```
primecart.cache.hit
```

Increment when Redis serves the request.

---

## Cache Miss

```
primecart.cache.miss
```

Increment when data is loaded from the database.

Useful KPI:

```
Cache Hit Ratio = Hits / (Hits + Misses)
```

---

## Validation Failure

```
primecart.product.validation.failed
```

Increment for invalid requests.

Examples:

- Empty product name
- Negative price
- Missing category

---

## Duplicate SKU

```
primecart.product.duplicate.sku
```

Increment whenever duplicate SKU validation fails.

---

# Timer Metrics

## Product Creation Time

```
primecart.product.create.time
```

Measure end-to-end product creation latency.

Typical dashboard:

- Average
- Max
- P95
- P99

---

# Distribution Summary

## Product Price Distribution

```
primecart.product.price
```

Record each product price.

Useful statistics:

- Average price
- Maximum price
- Minimum price
- Percentiles

---

# Gauge Metrics

## Total Products

```
primecart.products.total
```

Current number of products.

---

## Active Products

```
primecart.products.active
```

Current active products.

---

## Low Stock Products

```
primecart.products.lowstock
```

Current products below the configured stock threshold.

---

## Out Of Stock Products

```
primecart.products.outofstock
```

Current products with zero stock.

---

# Default Metrics Already Available

Spring Boot Actuator already exposes:

- HTTP request metrics
- JVM memory
- CPU
- Thread metrics
- Garbage collection
- Database connection pool
- HikariCP metrics
- Disk usage
- Process uptime

No additional implementation is required for these.

---

# Grafana Dashboard Suggestions

Create panels for:

1. Products Created
2. Products Updated
3. Products Deleted
4. Product Not Found Rate
5. Cache Hit Ratio
6. Product Creation Latency (P95)
7. Total Products
8. Active Products
9. Low Stock Products
10. Out Of Stock Products

---

# Interview Explanation

> In the Product Service, I implemented custom Micrometer metrics in addition to the default Actuator metrics. These
> metrics track business events such as product creation, updates, cache hit/miss ratio, validation failures, and
> inventory status. The metrics are scraped by Prometheus and visualized in Grafana, enabling operational monitoring,
> performance analysis, and business insights.

---

# Recommended Metrics for PrimeCart

- ✅ Product Created
- ✅ Product Updated
- ✅ Product Deleted
- ✅ Product Not Found
- ✅ Cache Hit
- ✅ Cache Miss
- ✅ Product Creation Time
- ✅ Total Products
- ✅ Active Products
- ✅ Low Stock Products