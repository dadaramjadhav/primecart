# Inventory Stock Metrics and Alerting

## 1. Purpose

PrimeCart Inventory Service exposes the current available quantity of each
product as a custom Micrometer metric. Prometheus scrapes the metric, Grafana
visualizes it, and Grafana-managed alert rules notify the configured Gmail
contact point when a product is low on stock or out of stock.

The monitoring flow is:

```text
Inventory table
    |
    | scheduled read
    v
InventoryStockMetrics
    |
    | Micrometer MultiGauge
    v
/actuator/prometheus
    |
    | scrape
    v
Prometheus
    |
    +--> Grafana dashboard
    |
    +--> Low-stock alert
    |
    +--> Out-of-stock alert
             |
             v
       Gmail notification
```

## 2. Implementation

The implementation is located at:

```text
inventory-service/src/main/java/com/primecart/metrics/InventoryStockMetrics.java
```

It registers a `MultiGauge` named:

```text
primecart.inventory.available.quantity
```

Micrometer exposes it to Prometheus as:

```text
primecart_inventory_available_quantity
```

Each inventory item is identified by bounded product labels:

```text
product_id
sku
```

Example:

```prometheus
primecart_inventory_available_quantity{
  job="inventory-service",
  product_id="1",
  sku="SKU-001"
} 8
```

The gauge represents current state. It can increase or decrease, unlike a
counter, which only increases.

## 3. Scheduled Refresh

Scheduling is enabled on the Inventory Service application:

```java
@SpringBootApplication
@EnableScheduling
public class InventoryServiceApplication {
    // ...
}
```

`InventoryStockMetrics` periodically reads all inventory records and replaces
the current `MultiGauge` rows:

```java
@Transactional(readOnly = true)
@Scheduled(
        fixedDelayString =
                "${primecart.metrics.inventory.refresh-interval-ms:30000}",
        initialDelayString =
                "${primecart.metrics.inventory.initial-delay-ms:5000}"
)
public void refreshAvailableQuantities() {

    List<? extends MultiGauge.Row<?>> rows = inventoryRepository
            .findAll()
            .stream()
            .map(this::toGaugeRow)
            .toList();

    availableQuantityGauge.register(rows, true);
}
```

Default timing:

| Setting | Default |
| --- | ---: |
| Initial refresh delay | 5 seconds |
| Delay between completed refreshes | 30 seconds |
| Prometheus scrape interval | 15 seconds |

The configuration can be overridden in Inventory Service configuration:

```yaml
primecart:
  metrics:
    inventory:
      refresh-interval-ms: 30000
      initial-delay-ms: 5000
```

The metric is eventually consistent with the inventory table. With the default
settings, a database change may take approximately 45 seconds to reach
Prometheus: up to 30 seconds for gauge refresh and up to 15 seconds for the
next scrape. Grafana evaluation and the alert pending period add further delay
before notification.

## 4. Verification

Verify the raw Inventory Service metric:

```bash
curl -s http://localhost:8084/actuator/prometheus \
  | grep primecart_inventory_available_quantity
```

Query all current quantities in Prometheus:

```promql
primecart_inventory_available_quantity{
  job="inventory-service"
}
```

Query products below the low-stock threshold:

```promql
primecart_inventory_available_quantity{
  job="inventory-service"
} < 10
```

Query products that are out of stock:

```promql
primecart_inventory_available_quantity{
  job="inventory-service"
} < 1
```

Prometheus comparisons without the `bool` modifier return only the product
series that satisfy the condition while retaining the original quantity as
the sample value.

## 5. Grafana Low-Stock Alert

The low-stock rule is a Grafana-managed alert backed by Prometheus.

Recommended rule configuration:

| Setting | Value |
| --- | --- |
| Rule name | `PrimeCart Product Low Stock` |
| Data source | Prometheus |
| Query type | Instant |
| Condition | Query value is below `10` |
| Severity | `warning` |
| Evaluation interval | 30 seconds |
| Pending period | 1 minute |
| Keep firing for | 1 minute |

Alert query:

```promql
primecart_inventory_available_quantity{
  job="inventory-service"
}
```

The query must not be aggregated with `sum()`. Keeping the `product_id` and
`sku` labels lets Grafana create an independent alert instance for each
low-stock product.

Suggested labels:

```text
severity=warning
category=inventory
team=operations
environment=local
```

Suggested annotations:

```gotemplate
Summary:
Low stock for SKU {{ $labels.sku }}

Description:
Product {{ $labels.product_id }} (SKU {{ $labels.sku }})
has {{ $values.A.Value }} available units.
```

## 6. Grafana Out-of-Stock Alert

Out of stock is treated as more urgent than low stock.

Recommended rule configuration:

| Setting | Value |
| --- | --- |
| Rule name | `PrimeCart Product Out of Stock` |
| Data source | Prometheus |
| Query type | Instant |
| Condition | Query value is below `1` |
| Severity | `critical` |
| Evaluation interval | 30 seconds |
| Pending period | 0 or 30 seconds |
| Keep firing for | 1 minute |

Alert query:

```promql
primecart_inventory_available_quantity{
  job="inventory-service"
}
```

Suggested annotations:

```gotemplate
Summary:
SKU {{ $labels.sku }} is out of stock

Description:
Product {{ $labels.product_id }} has
{{ $values.A.Value }} available units.
```

The two rules produce different operational signals:

```text
Quantity 1-9  -> Low-stock warning
Quantity 0    -> Out-of-stock critical alert
Quantity 10+  -> Normal
```

When quantity is zero, both rules can match. Notification policy grouping or
inhibition should be used in a production alerting setup to avoid sending a
warning and a critical notification for the same product.

## 7. Gmail Notifications

Grafana SMTP uses Gmail with a Google App Password. The App Password and email
account are supplied through environment variables and must not be committed:

```yaml
GF_SMTP_ENABLED: "true"
GF_SMTP_HOST: "smtp.gmail.com:587"
GF_SMTP_USER: "${GRAFANA_SMTP_USER}"
GF_SMTP_PASSWORD: "${GRAFANA_SMTP_APP_PASSWORD}"
GF_SMTP_FROM_ADDRESS: "${GRAFANA_SMTP_USER}"
GF_SMTP_FROM_NAME: "PrimeCart Grafana"
GF_SMTP_SKIP_VERIFY: "false"
GF_SMTP_STARTTLS_POLICY: "MandatoryStartTLS"
```

The real values belong in the ignored observability `.env` file, Vault,
Docker secrets, or the deployment platform's secret store:

```dotenv
GRAFANA_SMTP_USER=sender@example.com
GRAFANA_SMTP_APP_PASSWORD=replace-with-app-password
```

The alert rules use the configured Gmail contact point. Resolved notifications
remain enabled so the recipient is informed when stock returns to a normal
level.

## 8. Dashboard Panel

A useful dashboard panel displays every currently low-stock product.

Query:

```promql
primecart_inventory_available_quantity{
  job="inventory-service"
} < 10
```

Recommended visualization:

| Option | Value |
| --- | --- |
| Visualization | Table |
| Query type | Instant |
| Title | `Low Stock Products` |
| Unit | Short |
| Decimals | 0 |

Display:

```text
sku
product_id
Value
```

Suggested value colors:

| Quantity | Color |
| ---: | --- |
| 0 | Red |
| 1-4 | Orange |
| 5-9 | Yellow |
| 10 or more | Green |

## 9. Test Procedure

1. Select an existing inventory item.
2. Decrease its available quantity to a value between 1 and 9.
3. Wait for the scheduled metric refresh and Prometheus scrape.
4. Confirm that Prometheus reports the new value.
5. Observe the low-stock rule transition from `Normal` to `Pending` and then
   `Firing`.
6. Confirm receipt of the warning email.
7. Decrease the quantity to zero.
8. Confirm the critical out-of-stock alert and email.
9. Increase the quantity to at least 10.
10. Confirm that both alerts resolve and recovery notifications are delivered.

For controlled testing, verify the metric before waiting for Grafana:

```promql
primecart_inventory_available_quantity{
  job="inventory-service",
  sku="SKU-001"
}
```

## 10. Cardinality and Production Considerations

The gauge creates one Prometheus series per unique combination of:

```text
job
instance
product_id
sku
```

This is acceptable for PrimeCart's small portfolio catalog. It would be
expensive for a catalog containing hundreds of thousands or millions of
products.

For a large production catalog, prefer one or more of:

- An aggregate gauge containing the number of low-stock products
- Low-stock business events consumed by a replenishment workflow
- Database-driven stock reports
- Metrics grouped by warehouse or category
- A dedicated inventory analytics or alerting pipeline

Additional production improvements:

- Replace `findAll()` with a projection containing only product ID, SKU, and
  available quantity.
- Avoid overlapping scheduled refreshes in a multi-instance deployment.
- Define alert thresholds per product/category outside Prometheus if business
  thresholds differ.
- Provision Grafana dashboards, alert rules, and notification policies from
  version-controlled files.
- Add inhibition so a critical out-of-stock alert suppresses the low-stock
  warning for the same product.
- Protect Prometheus and Actuator endpoints on a private monitoring network.

## 11. Interview Summary

> Inventory Service publishes a custom Micrometer `MultiGauge` that represents
> the current available quantity for each product. A scheduled, read-only
> database refresh updates the gauge, and Prometheus scrapes it through Spring
> Boot Actuator. Grafana uses the product and SKU labels to create separate
> low-stock and out-of-stock alert instances and sends warning or critical
> notifications through a Gmail contact point. I kept the design appropriate
> for the project's small catalog and documented that per-product labels would
> require a different approach at large production cardinality.
