# PrimeCart application deployment on local Kubernetes

This document records the completed deployment of the PrimeCart platform and
application services to Docker Desktop Kubernetes on macOS.

## Scope

The application layer contains:

| Component | Kubernetes service | Port |
|---|---|---:|
| Config Server | `config-server` | 8888 |
| Spring Boot Admin | `sb-admin-server` | 9191 |
| Product Service | `product-service` | 8081 |
| Order Service | `order-service` | 8082 |
| Cart Service | `cart-service` | 8083 |
| Inventory Service | `inventory-service` | 8084 |
| Payment Service | `payment-service` | 8085 |
| Customer Service | `customer-service` | 8086 |
| API Gateway | `api-gateway` | 8181 |
| PrimeCart UI | `primecart-ui` | 80 |

All application resources run in the `primecart-app` namespace.

## Prerequisites

Deploy and verify these dependencies before deploying the applications:

- Namespaces
- MySQL and application databases
- Redis
- RabbitMQ
- Keycloak with the `primecart` realm
- Prometheus and Grafana
- Tempo
- OpenTelemetry Collector
- Logstash
- Config Server
- Spring Boot Admin

Verify the namespaces:

```bash
kubectl get namespaces \
  primecart-infra \
  primecart-observe \
  primecart-app
```

Verify the dependencies:

```bash
kubectl get pods --namespace primecart-infra
kubectl get pods --namespace primecart-observe
kubectl get pods --namespace primecart-app
```

## Required secrets

Application pods reference secrets in their own namespace. Confirm that the
following secrets exist in `primecart-app`:

```bash
kubectl get secrets --namespace primecart-app
```

Required secrets include:

```text
mysql-credentials
rabbitmq-credentials
redis-credentials
config-server-credentials
product-service-credentials
```

Secrets are namespace-scoped. A secret in `primecart-infra` cannot be referenced
directly by a pod in `primecart-app`.

## Local container images

The current manifests use images imported directly into the Docker Desktop
Kubernetes node:

```text
primecart/product-service:local-v2
primecart/customer-service:local-v1
primecart/inventory-service:local-v1
primecart/cart-service:local-v1
primecart/order-service:local-v1
primecart/payment-service:local-v1
primecart/api-gateway:local-v5
primecart/primecart-ui:local-v3
```

The manifests use:

```yaml
imagePullPolicy: Never
```

Build a Spring Boot service:

```bash
./<service>/mvnw \
  --file <service>/pom.xml \
  clean package \
  -DskipTests
```

Build its image:

```bash
docker build \
  --no-cache \
  --tag primecart/<service>:<tag> \
  <service>
```

Import the image into Docker Desktop Kubernetes:

```bash
docker save primecart/<service>:<tag> |
docker exec --interactive desktop-control-plane \
  ctr --namespace k8s.io images import -
```

Build the UI:

```bash
docker build \
  --no-cache \
  --tag primecart/primecart-ui:local-v3 \
  primecart-ui
```

## Kubernetes manifests

Application manifests are stored under:

```text
k8s/applications/
├── api-gateway/api-gateway.yaml
├── cart-service/cart-service.yaml
├── customer-service/customer-service.yaml
├── inventory-service/inventory-service.yaml
├── order-service/order-service.yaml
├── payment-service/payment-service.yaml
├── primecart-ui/primecart-ui.yaml
└── product-service/product-service.yaml
```

Platform manifests are stored under:

```text
k8s/platform/
├── config-server/config-server.yaml
└── sb-admin-server/sb-admin-server.yaml
```

Each backend application manifest contains:

- A ClusterIP `Service`
- A `Deployment`
- Environment-specific dependency addresses
- Startup, readiness, and liveness probes
- CPU and memory requests and limits
- A restricted container security context
- A writable `/tmp` `emptyDir`
- A Prometheus `ServiceMonitor`

## Deployment order

Deploy the platform first:

```bash
kubectl apply \
  --filename k8s/platform/config-server/config-server.yaml

kubectl rollout status deployment/config-server \
  --namespace primecart-app \
  --timeout=10m
```

```bash
kubectl apply \
  --filename k8s/platform/sb-admin-server/sb-admin-server.yaml

kubectl rollout status deployment/sb-admin-server \
  --namespace primecart-app \
  --timeout=10m
```

Deploy applications in dependency order:

```bash
kubectl apply \
  --filename k8s/applications/product-service/product-service.yaml

kubectl apply \
  --filename k8s/applications/customer-service/customer-service.yaml

kubectl apply \
  --filename k8s/applications/inventory-service/inventory-service.yaml

kubectl apply \
  --filename k8s/applications/cart-service/cart-service.yaml

kubectl apply \
  --filename k8s/applications/order-service/order-service.yaml

kubectl apply \
  --filename k8s/applications/payment-service/payment-service.yaml

kubectl apply \
  --filename k8s/applications/api-gateway/api-gateway.yaml

kubectl apply \
  --filename k8s/applications/primecart-ui/primecart-ui.yaml
```

Watch all application pods:

```bash
kubectl get pods \
  --namespace primecart-app \
  --watch
```

`kubectl apply` creates or updates the resources and automatically starts a
rollout when the pod template changes. `kubectl rollout status` is optional and
only waits for and reports the rollout result.

## Internal service addresses

Applications must not use `localhost` to connect to another Kubernetes
application. Inside a pod, `localhost` refers to that same pod.

Examples:

```text
http://config-server:8888
http://sb-admin-server:9191
http://product-service:8081
http://order-service:8082
http://cart-service:8083
http://inventory-service:8084
http://payment-service:8085
http://customer-service:8086
```

Cross-namespace dependencies use Kubernetes DNS:

```text
mysql.primecart-infra:3306
redis.primecart-infra:6379
rabbitmq.primecart-infra:5672
keycloak.primecart-infra:8080
otel-collector.primecart-observe:4318
logstash.primecart-observe:5000
```

## Config Server overrides

Each application supports an environment-specific Config Server import:

```yaml
spring:
  config:
    import: ${SPRING_CONFIG_IMPORT:configserver:http://localhost:8888}
```

The Kubernetes manifests provide:

```yaml
- name: SPRING_CONFIG_IMPORT
  value: configserver:http://config-server:8888
```

Configuration stored in the PrimeCart configuration repository must also use
Kubernetes service addresses for API Gateway routes:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product-service
          uri: http://product-service:8081
        - id: order-service
          uri: http://order-service:8082
        - id: cart-service
          uri: http://cart-service:8083
        - id: inventory-service
          uri: http://inventory-service:8084
        - id: payment-service
          uri: http://payment-service:8085
        - id: customer-service
          uri: http://customer-service:8086
```

After pushing Config Server repository changes:

```bash
kubectl rollout restart deployment/config-server \
  --namespace primecart-app

kubectl rollout status deployment/config-server \
  --namespace primecart-app \
  --timeout=10m

kubectl rollout restart deployment/api-gateway \
  --namespace primecart-app
```

## Keycloak and JWT configuration

Browser tokens use this issuer:

```text
http://localhost:8080/realms/primecart
```

Application pods download signing keys using the internal Keycloak service:

```text
http://keycloak.primecart-infra:8080/realms/primecart/protocol/openid-connect/certs
```

The manifests therefore separate issuer validation and JWK retrieval:

```yaml
- name: SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI
  value: http://localhost:8080/realms/primecart

- name: SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI
  value: http://keycloak.primecart-infra:8080/realms/primecart/protocol/openid-connect/certs
```

The custom JWT decoders use `withJwkSetUri(jwkSetUri)` while separately
validating the public issuer.

Keycloak uses a stable browser-facing hostname:

```yaml
- name: KC_HOSTNAME
  value: http://localhost:8080
```

Order Service and Payment Service request service-account tokens through the
internal Kubernetes endpoint:

```yaml
- name: SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_KEYCLOAK_TOKEN_URI
  value: http://keycloak.primecart-infra:8080/realms/primecart/protocol/openid-connect/token
```

## Service-to-service clients

Feign clients use configurable URLs instead of hardcoded localhost addresses.

Examples:

```text
Cart Service      -> Product Service
Order Service     -> Cart Service
Order Service     -> Inventory Service
Payment Service   -> Order Service
```

The Kubernetes manifests override the client URLs:

```yaml
- name: PRIMECART_CLIENTS_PRODUCT_SERVICE_URL
  value: http://product-service:8081

- name: PRIMECART_CLIENTS_CART_SERVICE_URL
  value: http://cart-service:8083

- name: PRIMECART_CLIENTS_INVENTORY_SERVICE_URL
  value: http://inventory-service:8084

- name: PRIMECART_CLIENTS_ORDER_SERVICE_URL
  value: http://order-service:8082
```

## Prometheus registration

Every monitored Kubernetes `Service` must carry the label selected by its
`ServiceMonitor`:

```yaml
metadata:
  labels:
    app.kubernetes.io/name: order-service
```

The corresponding `ServiceMonitor` is created in `primecart-observe`:

```yaml
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: order-service
  namespace: primecart-observe
  labels:
    release: monitoring
spec:
  namespaceSelector:
    matchNames:
      - primecart-app
  selector:
    matchLabels:
      app.kubernetes.io/name: order-service
  endpoints:
    - port: http
      path: /actuator/prometheus
      interval: 15s
      scrapeTimeout: 10s
```

Verify all ServiceMonitors:

```bash
kubectl get servicemonitors \
  --namespace primecart-observe
```

Verify Prometheus targets:

```bash
curl --silent \
  http://localhost:9090/api/v1/targets |
jq '
  .data.activeTargets[]
  | select(.labels.namespace == "primecart-app")
  | {
      service: .labels.service,
      health,
      scrapeUrl,
      lastError
    }
'
```

## Port-forwarding

Start all local access endpoints from one terminal:

```bash
./scripts/primecart-port-forward.sh
```

Local endpoints:

| Component | Address |
|---|---|
| PrimeCart UI | `http://localhost:5173` |
| API Gateway | `http://localhost:8181` |
| Keycloak | `http://localhost:8080` |
| MySQL | `127.0.0.1:3307` |
| RabbitMQ AMQP | `127.0.0.1:5672` |
| RabbitMQ Management | `http://localhost:15672` |
| Redis Insight | `http://localhost:5540` |
| Prometheus | `http://localhost:9090` |
| Grafana | `http://localhost:3000` |
| Spring Boot Admin | `http://localhost:9191` |

Press `Ctrl+C` in the script terminal to stop all port-forwards.

## Sanity checks

Check all pods:

```bash
kubectl get pods \
  --namespace primecart-app
```

Check all Services and endpoints:

```bash
kubectl get services,endpoints \
  --namespace primecart-app
```

Check API Gateway health:

```bash
curl --fail \
  http://localhost:8181/actuator/health
```

Check API Gateway routes:

```bash
curl --silent \
  http://localhost:8181/actuator/gateway/routes |
jq '.[] | {route_id, uri}'
```

Request a Keycloak token:

```bash
ACCESS_TOKEN=$(curl --silent --show-error --fail \
  --request POST \
  http://localhost:8080/realms/primecart/protocol/openid-connect/token \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'grant_type=password' \
  --data-urlencode 'client_id=primecart-react' \
  --data-urlencode "username=${KEYCLOAK_USER}" \
  --data-urlencode "password=${KEYCLOAK_USER_PASSWORD}" |
  jq --raw-output '.access_token')
```

Test Product Service through API Gateway:

```bash
curl --include \
  http://localhost:8181/api/products \
  --header "Authorization: Bearer ${ACCESS_TOKEN}"
```

## End-to-end test

Open:

```bash
open http://localhost:5173
```

Test:

```text
Login
-> Browse products
-> Add a product to the cart
-> Create an order
-> Reserve inventory
-> Create the payment
-> Complete the payment
-> Confirm inventory
-> Confirm the order
```

Monitor the saga:

```bash
kubectl logs deployment/order-service \
  --namespace primecart-app \
  --container order-service \
  --follow
```

```bash
kubectl logs deployment/inventory-service \
  --namespace primecart-app \
  --container inventory-service \
  --follow
```

```bash
kubectl logs deployment/payment-service \
  --namespace primecart-app \
  --container payment-service \
  --follow
```

## Troubleshooting

Get the newest pod:

```bash
kubectl get pods \
  --namespace primecart-app \
  --sort-by=.metadata.creationTimestamp
```

Inspect pod events:

```bash
kubectl describe pod <pod-name> \
  --namespace primecart-app
```

Inspect current and previous logs:

```bash
kubectl logs <pod-name> \
  --namespace primecart-app \
  --tail=300

kubectl logs <pod-name> \
  --namespace primecart-app \
  --previous \
  --tail=300
```

Common resolved issues:

| Symptom | Root cause | Resolution |
|---|---|---|
| Config client connection refused | Config Server URL used `localhost` | Use `config-server:8888` |
| JWT issuer invalid | Browser and internal Keycloak URLs differed | Validate localhost issuer and use internal JWK URL |
| OAuth token request refused | Service-account token URL used `localhost` | Use the internal Keycloak token URL |
| Gateway returned 503 | Routes pointed to localhost | Use Kubernetes Service URLs |
| Redis readiness failure | Redis host defaulted to localhost | Use `redis.primecart-infra` |
| Prometheus target missing | Service did not have the label selected by ServiceMonitor | Add matching Service metadata labels |
| Payment initially returned 404 | Payment creation is asynchronous | Retry the payment query briefly |
| New orders matched sample payments | Sample payment `order_id` values collided with live orders | Remove conflicting local sample payment records |

## Future CI image migration

The current setup imports local images manually. The planned improvement is:

```text
CI builds multi-architecture image
-> CI pushes immutable SHA tag to GHCR
-> Kubernetes manifest references the GHCR image
-> CD applies the manifest
-> CD waits for rollout and runs health checks
```

Example future image:

```yaml
image: ghcr.io/dadaramjadhav/primecart-order-service:sha-<commit>
imagePullPolicy: IfNotPresent
```

Use immutable commit SHA tags rather than `latest`.
