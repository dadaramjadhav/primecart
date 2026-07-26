# PrimeCart

PrimeCart is a full-stack e-commerce application built as a collection of Spring Boot microservices with a React frontend. The project includes authentication, centralized configuration, asynchronous messaging, caching, observability, CI-generated multi-platform images, and Kubernetes deployment using Kustomize.

## Architecture

```text
Browser
   |
   v
NGINX Ingress
   |
   +-- primecart.localhost ----------> PrimeCart UI
   +-- auth.primecart.localhost -----> Keycloak
   +-- api.primecart.localhost ------> API Gateway
                                          |
                                          v
                    +---------------------+---------------------+
                    |                     |                     |
              Product Service       Cart Service         Order Service
              Customer Service      Inventory Service    Payment Service
                    |                     |                     |
                    +---------- MySQL / Redis / RabbitMQ -------+

Applications --> OpenTelemetry Collector --> Tempo
Applications --> Prometheus --> Grafana
Applications --> Logstash --> Splunk
```

Kubernetes Deployments and StatefulSets manage the workloads. Services provide stable internal networking, Ingress exposes selected HTTP endpoints, and the API Gateway handles application API routing and security.

## Services

| Component | Responsibility |
|---|---|
| PrimeCart UI | React customer and administration interface |
| API Gateway | External API entry point, authentication, CORS, and routing |
| Config Server | Centralized Spring configuration |
| Spring Boot Admin | Application health and runtime monitoring |
| Product Service | Product catalogue |
| Cart Service | Shopping-cart operations |
| Customer Service | Customer information |
| Inventory Service | Stock management and reservation |
| Order Service | Order workflow and service coordination |
| Payment Service | Payment workflow |
| Keycloak | Identity and access management |

## Technology stack

- Java 17 and Spring Boot
- Spring Cloud Config, Gateway, OpenFeign, and Resilience4j
- Spring Security OAuth 2.0 with Keycloak
- React, Vite, Material UI, Redux Toolkit, and TanStack Query
- MySQL, Flyway, Redis, and RabbitMQ
- Prometheus, Grafana, OpenTelemetry Collector, Tempo, Logstash, and Splunk
- Docker, Kubernetes, Helm, Kustomize, and NGINX Ingress
- GitHub Actions and Docker Hub multi-platform images

## Repository structure

```text
primecart/
├── api-gateway/
├── cart-service/
├── config-server/
├── customer-service/
├── inventory-service/
├── order-service/
├── payment-service/
├── product-service/
├── sb-admin-server/
├── primecart-ui/
├── k8s/
│   ├── applications/
│   ├── base/
│   ├── infrastructure/
│   ├── namespaces/
│   ├── networking/
│   ├── observability/
│   ├── overlays/
│   └── platform/
├── scripts/
├── devops/
└── docs/
```

## Prerequisites

The local Kubernetes environment requires:

- Docker Desktop with Kubernetes enabled
- `kubectl`
- Helm
- Docker Compose
- `jq`
- `curl`
- Access to the PrimeCart images on Docker Hub

Verify the tools and cluster:

```bash
docker version
kubectl cluster-info
helm version
jq --version
```

## Local Kubernetes deployment

The deployment scripts expect the required Kubernetes Secrets to exist before infrastructure is installed. Do not commit credentials or generated database and Keycloak backups to Git.

Detailed component setup is available under [`docs/k8s`](docs/k8s).

### 1. Verify the configured images

```bash
./scripts/verify-images.sh
```

### 2. Deploy the complete stack

```bash
./scripts/deploy-primecart.sh
```

This performs the following operations:

1. Starts Splunk using Docker Compose.
2. Verifies the configured Docker Hub images.
3. Creates namespaces.
4. Deploys infrastructure and observability.
5. Deploys Config Server and Spring Boot Admin.
6. Deploys the application services and UI.
7. Waits for application rollouts.
8. Installs NGINX Ingress and applies the local Ingress resources.
9. Displays the final workload status.

Override the rollout timeout when necessary:

```bash
TIMEOUT=20m ./scripts/deploy-primecart.sh
```

### 3. Run the sanity checks

```bash
./scripts/sanity-check.sh
```

### 4. Open the application

| Endpoint | URL |
|---|---|
| PrimeCart UI | <http://primecart.localhost> |
| API Gateway | <http://api.primecart.localhost> |
| Keycloak | <http://auth.primecart.localhost> |
| Grafana | <http://grafana.primecart.localhost> |
| Prometheus | <http://prometheus.primecart.localhost> |
| Spring Boot Admin | <http://sbadmin.primecart.localhost> |

The local setup also contains direct service Ingress routes for diagnostics. Normal client traffic should pass through the API Gateway.

## Deployment management

Display the current state:

```bash
./scripts/status-primecart.sh
```

Restart the stack without deleting resources:

```bash
./scripts/restart-primecart.sh
```

Stop workloads while preserving manifests and persistent data:

```bash
./scripts/stop-primecart.sh
```

Follow the logs of one application:

```bash
./scripts/apps/logs-primecart-app.sh payment-service
```

Back up MySQL and Keycloak before destructive operations:

```bash
./scripts/backup-primecart.sh
```

## Port forwarding

Ingress is the preferred local HTTP entry point. Port forwarding remains useful for TCP services, management interfaces, and debugging:

```bash
./scripts/primecart-port-forward.sh
```

Press `Ctrl+C` in that terminal to stop all port forwards.

## Image version management

GitHub Actions publishes PrimeCart images to Docker Hub using the Git commit SHA:

```text
dadaramjadhav/primecart-product-service:<git-sha>
```

Each published tag is a multi-platform image supporting:

```text
linux/amd64
linux/arm64
```

Update all application and platform image tags after CI completes:

```bash
GIT_SHA="$(git rev-parse HEAD)"

./scripts/apps/update-app-version.sh "${GIT_SHA}"
./scripts/platform/update-platform-version.sh "${GIT_SHA}"
./scripts/verify-images.sh
```

Deploy the updated workloads:

```bash
./scripts/apps/deploy-primecart-apps.sh
./scripts/platform/deploy-platform.sh
```

## Kustomize environments

The repository provides environment overlays:

```text
k8s/overlays/local
k8s/overlays/dev
k8s/overlays/prod
```

Render an overlay before applying it:

```bash
kubectl kustomize k8s/overlays/local
```

Apply the local overlay:

```bash
kubectl apply --kustomize k8s/overlays/local
```

See the [Kustomize setup guide](docs/k8s/kustomization/kustomization-setup.md) for image overrides, environment labels, platform overlays, and production replicas.

## Documentation

- [Continuous integration](docs/cicd/continuous-integration-setup.md)
- [Application deployment](docs/k8s/apps/application-deployment.md)
- [Ingress setup](docs/k8s/ingress/ingress-setup.md)
- [Kustomize setup](docs/k8s/kustomization/kustomization-setup.md)
- [MySQL setup](docs/k8s/mysql/mysql-setup.md)
- [Redis setup](docs/k8s/redis/redis-setup.md)
- [RabbitMQ setup](docs/k8s/rabbitmq/rabbitmq-setup.md)
- [Keycloak setup](docs/k8s/keycloak/keycloak-setup.md)
- [Observability setup](docs/k8s/o11y/observability-setup.md)
- [Tempo setup](docs/k8s/o11y/tempo-setup.md)
- [OpenTelemetry Collector setup](docs/k8s/otel-collector/otel-collector-setup.md)
- [Logstash setup](docs/k8s/o11y/logstash-setup.md)
- [Security controls](docs/security/security-controls.md)

## Security

- Never commit passwords, client secrets, access tokens, `.env` files, SQL backups, or exported Keycloak realms containing credentials.
- Store runtime credentials in Kubernetes Secrets or an external secret manager.
- Rotate a credential immediately if secret scanning detects it in a commit.
- Keep internal microservices private and expose application APIs through the API Gateway.

## Current deployment scope

The provided deployment is designed for local development on Docker Desktop Kubernetes. The `dev` and `prod` overlays establish the configuration structure, but a real remote deployment still requires environment-specific domains, TLS, storage classes, external secret management, and cloud infrastructure configuration.
