# PrimeCart Local Ingress Setup

## Purpose

PrimeCart uses ingress-nginx to expose local Kubernetes Services through
host-based HTTP routes. This reduces the number of browser-facing
`kubectl port-forward` processes required during local development.

The local hosts use the `.localhost` suffix, which resolves to the loopback
interface in modern browsers and operating systems. No `/etc/hosts` entries
are required.

## Components

```text
Browser
   |
   v
localhost:80
   |
   v
ingress-nginx controller
   |
   v
Kubernetes ClusterIP Service
   |
   v
Application pod
```

An Ingress resource does not run a proxy by itself. The ingress-nginx
controller watches Ingress resources and configures NGINX to route traffic to
their backend Services.

## Files

```text
k8s/
├── networking/
│   └── ingress/
│       ├── kustomization.yaml
│       └── primecart-local-ingress.yaml
└── overlays/
    └── local/
        └── ingress/
            └── kustomization.yaml

scripts/
└── ingress/
    └── deploy-ingress.sh
```

The networking Kustomization includes the Ingress manifest:

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization

resources:
  - primecart-local-ingress.yaml
```

The local overlay references the networking directory:

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization

resources:
  - ../../../networking/ingress
```

Referencing a directory containing a `kustomization.yaml` avoids Kustomize
load-restriction errors when a child overlay uses a resource outside its own
directory.

## Local hosts

Primary browser-facing routes:

| Host | Kubernetes Service | Namespace |
|---|---|---|
| `primecart.localhost` | `primecart-ui` | `primecart-app` |
| `api.primecart.localhost` | `api-gateway` | `primecart-app` |
| `auth.primecart.localhost` | `keycloak` | `primecart-infra` |
| `grafana.primecart.localhost` | `monitoring-grafana` | `primecart-observe` |
| `prometheus.primecart.localhost` | `monitoring-kube-prometheus-prometheus` | `primecart-observe` |
| `sbadmin.primecart.localhost` | `sb-admin-server` | `primecart-app` |

Local diagnostic routes:

| Host | Kubernetes Service |
|---|---|
| `product.primecart.localhost` | `product-service` |
| `cart.primecart.localhost` | `cart-service` |
| `customer.primecart.localhost` | `customer-service` |
| `inventory.primecart.localhost` | `inventory-service` |
| `order.primecart.localhost` | `order-service` |
| `payment.primecart.localhost` | `payment-service` |

Direct service routes are intended only for local health checks and debugging.
Normal browser and API traffic must use the API Gateway:

```text
http://api.primecart.localhost
```

Direct service Ingress routes bypass API Gateway authorization, route
restrictions, error handling, and other gateway policies. Do not copy them
into a production overlay.

## Install and deploy

Deploy ingress-nginx and the PrimeCart Ingress resources:

```bash
./scripts/ingress/deploy-ingress.sh
```

Use a longer timeout when the controller image must be downloaded:

```bash
TIMEOUT=15m ./scripts/ingress/deploy-ingress.sh
```

The script performs:

```text
Helm install or upgrade
→ Wait for ingress-nginx controller
→ Apply local Ingress Kustomization
→ Display configured hosts
```

The command is idempotent and can be run again after changing the Ingress
manifest.

## Manual ingress-nginx installation

```bash
helm upgrade --install ingress-nginx ingress-nginx \
  --repo https://kubernetes.github.io/ingress-nginx \
  --namespace ingress-nginx \
  --create-namespace \
  --wait \
  --timeout 10m
```

Wait for the controller:

```bash
kubectl rollout status deployment/ingress-nginx-controller \
  --namespace ingress-nginx \
  --timeout=10m
```

On Docker Desktop, the controller LoadBalancer normally exposes HTTP on
`localhost:80`.

## Apply Ingress resources manually

```bash
kubectl apply \
  --kustomize k8s/overlays/local/ingress
```

Check the routes:

```bash
kubectl get ingress \
  --all-namespaces \
  --output wide
```

Check the controller:

```bash
kubectl get pods,services \
  --namespace ingress-nginx
```

## Complete deployment integration

The complete deployment script calls the Ingress script after application
rollouts succeed:

```bash
./scripts/deploy-primecart.sh
```

Deployment order:

```text
Splunk
→ Image verification
→ Infrastructure
→ Observability
→ Platform
→ Applications
→ Ingress
→ Status
```

Ingress is applied after the applications so its backend Services and ready
pod endpoints already exist.

## Application configuration

PrimeCart UI production configuration uses the Ingress hosts:

```properties
VITE_API_BASE_URL=http://api.primecart.localhost
VITE_KEYCLOAK_URL=http://auth.primecart.localhost
VITE_KEYCLOAK_REALM=primecart
VITE_KEYCLOAK_CLIENT_ID=primecart-react
```

Vite variables are compiled into the static UI bundle during the Docker image
build. Changing them requires a new UI image and Kustomize image-tag update.

Keycloak uses:

```yaml
- name: KC_HOSTNAME
  value: http://auth.primecart.localhost

- name: KC_PROXY_HEADERS
  value: xforwarded
```

Backend JWT issuer validation must match the public issuer:

```text
http://auth.primecart.localhost/realms/primecart
```

Backend JWK and client-credentials token requests can continue using the
internal Keycloak Service URL.

The Keycloak UI client allows:

```text
Redirect URI: http://primecart.localhost/*
Web origin:   http://primecart.localhost
```

API Gateway CORS allows:

```text
http://primecart.localhost
```

After changing external Config Server CORS properties, restart only API
Gateway:

```bash
kubectl rollout restart deployment/api-gateway \
  --namespace primecart-app

kubectl rollout status deployment/api-gateway \
  --namespace primecart-app \
  --timeout=10m
```

## Verify primary routes

UI:

```bash
curl --include \
  http://primecart.localhost/
```

API Gateway:

```bash
curl --include \
  http://api.primecart.localhost/actuator/health
```

Keycloak:

```bash
curl --silent \
  http://auth.primecart.localhost/realms/primecart/.well-known/openid-configuration |
jq '{issuer, authorization_endpoint, token_endpoint}'
```

Grafana:

```bash
curl --include \
  http://grafana.primecart.localhost/api/health
```

Prometheus:

```bash
curl --include \
  http://prometheus.primecart.localhost/-/ready
```

Spring Boot Admin:

```bash
curl --include \
  http://sbadmin.primecart.localhost/actuator/health
```

## Verify diagnostic routes

```bash
curl --include \
  http://product.primecart.localhost/actuator/health
```

```bash
curl --include \
  http://cart.primecart.localhost/actuator/health
```

```bash
curl --include \
  http://customer.primecart.localhost/actuator/health
```

```bash
curl --include \
  http://inventory.primecart.localhost/actuator/health
```

```bash
curl --include \
  http://order.primecart.localhost/actuator/health
```

```bash
curl --include \
  http://payment.primecart.localhost/actuator/health
```

## Verify CORS

```bash
curl --include \
  --request OPTIONS \
  http://api.primecart.localhost/api/products \
  --header 'Origin: http://primecart.localhost' \
  --header 'Access-Control-Request-Method: GET' \
  --header 'Access-Control-Request-Headers: Authorization,Content-Type'
```

Expected response headers include:

```text
Access-Control-Allow-Origin: http://primecart.localhost
Access-Control-Allow-Credentials: true
```

## Open browser routes

```bash
open http://primecart.localhost/
open http://auth.primecart.localhost/
open http://grafana.primecart.localhost/
open http://prometheus.primecart.localhost/
open http://sbadmin.primecart.localhost/
```

## Troubleshoot 404

A controller-generated `404` normally means no Ingress rule matches the
request host and path.

```bash
kubectl get ingress \
  --all-namespaces
```

```bash
kubectl describe ingress <ingress-name> \
  --namespace <namespace>
```

Confirm that `ingressClassName` is:

```yaml
ingressClassName: nginx
```

## Troubleshoot 502 Bad Gateway

A `502` means the controller matched the Ingress but could not process a valid
backend response.

Check the backend endpoints:

```bash
kubectl get endpoints <service-name> \
  --namespace <namespace>
```

Check the backend pods:

```bash
kubectl get pods \
  --namespace <namespace> \
  --selector app.kubernetes.io/name=<application-name>
```

Check controller logs:

```bash
kubectl logs deployment/ingress-nginx-controller \
  --namespace ingress-nginx \
  --tail=200
```

The PrimeCart UI previously returned `502` because its Nginx configuration
generated a multiline Content Security Policy response header. HTTP header
values must not contain newline characters. Keep the CSP header on one line.

## Troubleshoot duplicate host and path

The ingress-nginx admission webhook rejects two Ingress resources that claim
the same host and path:

```text
host "<host>" and path "/" is already defined
```

Find the existing owner:

```bash
kubectl get ingress \
  --all-namespaces \
  --output custom-columns='NAMESPACE:.metadata.namespace,NAME:.metadata.name,HOST:.spec.rules[*].host'
```

Inspect and delete only the stale, incorrectly created Ingress:

```bash
kubectl get ingress <stale-name> \
  --namespace <stale-namespace> \
  --output yaml

kubectl delete ingress <stale-name> \
  --namespace <stale-namespace>
```

Apply the corrected manifest:

```bash
kubectl apply \
  --kustomize k8s/overlays/local/ingress
```

## Uninstall

Delete PrimeCart Ingress resources:

```bash
kubectl delete \
  --kustomize k8s/overlays/local/ingress
```

Uninstall ingress-nginx:

```bash
helm uninstall ingress-nginx \
  --namespace ingress-nginx
```

Delete the namespace only after the Helm release is removed:

```bash
kubectl delete namespace ingress-nginx
```

## Current limitations

```text
HTTP only
No TLS certificates
Local Docker Desktop LoadBalancer
Direct service hosts are for debugging only
Production domains are not configured
```

The next optional networking improvement is local HTTPS and TLS. For the
Java full-stack learning path, the next recommended topic after basic Ingress
is Kubernetes storage and persistence.
