# PrimeCart Kustomize Setup

## Purpose

PrimeCart uses Kustomize to reuse common Kubernetes manifests while selecting
environment-specific image versions, labels, and replica counts.

Kustomize is built into `kubectl`; a separate installation is not required.

## Directory structure

```text
k8s/
├── base/
│   └── applications/
│       ├── api-gateway/
│       ├── cart-service/
│       ├── customer-service/
│       ├── inventory-service/
│       ├── order-service/
│       ├── payment-service/
│       ├── product-service/
│       └── primecart-ui/
├── platform/
│   ├── config-server/
│   └── sb-admin-server/
└── overlays/
    ├── local/
    │   ├── applications/
    │   ├── platform/
    │   └── kustomization.yaml
    ├── dev/
    │   ├── applications/
    │   ├── platform/
    │   └── kustomization.yaml
    └── prod/
        ├── applications/
        ├── platform/
        └── kustomization.yaml
```

## Application bases

Each application directory contains its Kubernetes manifest and a
`kustomization.yaml`.

Example:

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization

resources:
  - product-service.yaml
```

The base Deployment contains the Docker Hub repository without an
environment-specific tag:

```yaml
containers:
  - name: product-service
    image: dadaramjadhav/primecart-product-service
    imagePullPolicy: IfNotPresent
```

Application bases include:

```text
API Gateway
Cart Service
Customer Service
Inventory Service
Order Service
Payment Service
Product Service
PrimeCart UI
```

## Platform bases

Platform resources are managed separately from business applications:

```text
k8s/platform/config-server
k8s/platform/sb-admin-server
```

Each platform directory also contains a `kustomization.yaml` that includes its
manifest.

The platform base images do not contain environment-specific tags:

```yaml
image: dadaramjadhav/primecart-config-server
imagePullPolicy: IfNotPresent
```

```yaml
image: dadaramjadhav/primecart-sb-admin-server
imagePullPolicy: IfNotPresent
```

## Environment overlays

PrimeCart currently defines three overlays:

```text
local
dev
prod
```

Each environment has two child overlays:

```text
applications
platform
```

The environment root combines both:

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization

resources:
  - applications
  - platform
```

Rendering the environment root therefore includes all application and platform
resources:

```bash
kubectl kustomize k8s/overlays/local
```

The infrastructure and observability manifests are not included in these
Kustomize overlays. They remain managed by the infrastructure deployment
script and their existing Helm or YAML configurations.

## Local overlay

The local application overlay imports all application bases and assigns
immutable CI image tags:

```yaml
images:
  - name: dadaramjadhav/primecart-api-gateway
    newTag: 8df8b612b2952f693e331f56202f9f9ae3d5a6f1

  - name: dadaramjadhav/primecart-ui
    newTag: a5e2b782908434f9954740a2d452531bb34d9b0d
```

The UI can have a different SHA because its CI workflow may run independently
from the Java service workflows.

The local platform overlay assigns immutable tags to Config Server and Spring
Boot Admin:

```yaml
images:
  - name: dadaramjadhav/primecart-config-server
    newTag: 8df8b612b2952f693e331f56202f9f9ae3d5a6f1

  - name: dadaramjadhav/primecart-sb-admin-server
    newTag: 8df8b612b2952f693e331f56202f9f9ae3d5a6f1
```

Local Deployments use one replica as defined by their base manifests.

## Development overlay

The development overlay has the same application and platform structure. It
currently selects the `main` image tag.

The `main` tag is convenient for a continuously updated development
environment, but it is mutable. Use an immutable Git SHA when a development
deployment must be reproducible.

The development overlay is scaffolding only until environment-specific
networking, URLs, credentials, and infrastructure are configured.

## Production overlay

The production overlay selects immutable Git SHA image tags.

It overrides application replica counts:

```yaml
replicas:
  - name: api-gateway
    count: 2

  - name: product-service
    count: 2

  - name: primecart-ui
    count: 2
```

All business application Deployments currently use two replicas in the
production overlay.

The production platform overlay uses:

```yaml
replicas:
  - name: config-server
    count: 2

  - name: sb-admin-server
    count: 1
```

Spring Boot Admin remains a single replica because its current application
registry is not configured for a highly available multi-replica deployment.

The production overlay is not ready for a real production cluster yet. The
base configuration still includes local assumptions such as `localhost`
Keycloak and UI URLs. Production requires environment-specific domains,
Ingress, TLS, Secrets, storage, and application configuration.

## Environment labels

Each environment root adds an identifying label:

```yaml
labels:
  - pairs:
      app.kubernetes.io/environment: local
    includeSelectors: false
    includeTemplates: true
```

The value changes by overlay:

```text
local -> app.kubernetes.io/environment: local
dev   -> app.kubernetes.io/environment: dev
prod  -> app.kubernetes.io/environment: prod
```

`includeTemplates: true` adds the label to pod templates.

`includeSelectors: false` prevents the environment label from changing
Deployment and Service selectors. This avoids immutable selector errors and
Service-to-pod routing problems.

Filter deployed resources by environment:

```bash
kubectl get pods \
  --all-namespaces \
  --selector app.kubernetes.io/environment=local
```

## Image-name matching

The image `name` in an overlay must exactly match the image repository in the
base manifest:

```yaml
# Base
image: dadaramjadhav/primecart-product-service

# Overlay
images:
  - name: dadaramjadhav/primecart-product-service
    newTag: <git-sha>
```

If the names differ, Kustomize will not apply `newTag`, and Kubernetes may try
to pull `latest`.

## Render and validate overlays

Render local:

```bash
kubectl kustomize \
  k8s/overlays/local \
  >/tmp/primecart-local.yaml
```

Render development:

```bash
kubectl kustomize \
  k8s/overlays/dev \
  >/tmp/primecart-dev.yaml
```

Render production:

```bash
kubectl kustomize \
  k8s/overlays/prod \
  >/tmp/primecart-prod.yaml
```

Review rendered images:

```bash
rg 'image:' \
  /tmp/primecart-local.yaml \
  /tmp/primecart-dev.yaml \
  /tmp/primecart-prod.yaml
```

Validate without changing the cluster:

```bash
kubectl apply \
  --dry-run=client \
  --filename /tmp/primecart-local.yaml
```

```bash
kubectl apply \
  --dry-run=client \
  --filename /tmp/primecart-dev.yaml
```

```bash
kubectl apply \
  --dry-run=client \
  --filename /tmp/primecart-prod.yaml
```

Remove temporary files:

```bash
rm \
  /tmp/primecart-local.yaml \
  /tmp/primecart-dev.yaml \
  /tmp/primecart-prod.yaml
```

## Script structure

```text
scripts/
├── apps/
│   ├── delete-primecart-apps.sh
│   ├── deploy-primecart-apps.sh
│   ├── logs-primecart-app.sh
│   ├── restart-primecart-apps.sh
│   ├── stop-primecart-apps.sh
│   ├── update-app-version.sh
│   └── wait-primecart-apps.sh
├── infra/
│   ├── deploy-primecart-infra.sh
│   └── stop-primecart-infra.sh
├── platform/
│   ├── deploy-platform.sh
│   ├── restart-platform.sh
│   ├── stop-platform.sh
│   ├── update-platform-version.sh
│   └── wait-platform.sh
├── backup-primecart.sh
├── deploy-primecart.sh
├── primecart-port-forward.sh
├── restart-primecart.sh
├── sanity-check.sh
├── status-primecart.sh
├── stop-primecart.sh
└── verify-images.sh
```

## Update application image versions

Update all local application image tags using one full Git SHA:

```bash
./scripts/apps/update-app-version.sh <40-character-git-sha>
```

Only use this script when every application image, including PrimeCart UI,
exists with that SHA. If the UI or one service was built separately, update
only its `newTag` in the application overlay.

Review:

```bash
git diff -- \
  k8s/overlays/local/applications/kustomization.yaml
```

## Update platform image versions

```bash
./scripts/platform/update-platform-version.sh <40-character-git-sha>
```

Only use the same SHA for Config Server and Spring Boot Admin when both CI
workflows published that tag.

## Verify Docker images

Verify that every image referenced by the local application and platform
overlays exists:

```bash
./scripts/verify-images.sh
```

Run this before deployment to prevent avoidable `ImagePullBackOff` failures.

## Deploy local applications

```bash
./scripts/apps/deploy-primecart-apps.sh
```

Wait for all application rollouts:

```bash
./scripts/apps/wait-primecart-apps.sh
```

Use a longer timeout:

```bash
TIMEOUT=15m ./scripts/apps/wait-primecart-apps.sh
```

## Deploy local platform

```bash
./scripts/platform/deploy-platform.sh
```

Wait separately when needed:

```bash
./scripts/platform/wait-platform.sh
```

## Deploy the complete local stack

```bash
./scripts/deploy-primecart.sh
```

The top-level deployment flow is:

```text
Splunk
→ Infrastructure
→ Observability
→ Platform
→ Applications
→ Rollout checks
→ Status
```

Start local port-forwarding separately:

```bash
./scripts/primecart-port-forward.sh
```

Run health checks:

```bash
./scripts/sanity-check.sh
```

## Restart

Restart the complete platform and application layer:

```bash
./scripts/restart-primecart.sh
```

Restart applications only:

```bash
./scripts/apps/restart-primecart-apps.sh
```

Restart platform only:

```bash
./scripts/platform/restart-platform.sh
```

## Stop

Stop the complete local stack while preserving Kubernetes resources and
persistent data:

```bash
./scripts/stop-primecart.sh
```

Stop applications only:

```bash
./scripts/apps/stop-primecart-apps.sh
```

Stop platform only:

```bash
./scripts/platform/stop-platform.sh
```

## Delete applications

Delete application resources only when a clean recreation is required:

```bash
./scripts/apps/delete-primecart-apps.sh
```

Recreate them:

```bash
./scripts/apps/deploy-primecart-apps.sh
./scripts/apps/wait-primecart-apps.sh
```

Deleting Services terminates their active port-forward processes. Restart
port-forwarding after recreation:

```bash
./scripts/primecart-port-forward.sh
```

## Troubleshoot ImagePullBackOff

Render and inspect the selected images:

```bash
kubectl kustomize \
  k8s/overlays/local |
rg 'image:'
```

Verify registry availability:

```bash
./scripts/verify-images.sh
```

Inspect the failing pod:

```bash
kubectl get pods \
  --namespace primecart-app

kubectl describe pod <pod-name> \
  --namespace primecart-app
```

Common causes:

- The overlay image name does not exactly match the base image.
- The Git SHA does not exist in the service's Docker Hub repository.
- A private repository requires an image pull Secret.
- The image does not support the Kubernetes node architecture.

Correct the image name or tag, then apply the overlay again. Deleting the
Deployment is not required.

## Recommended local workflow

```bash
./scripts/verify-images.sh
./scripts/deploy-primecart.sh
./scripts/primecart-port-forward.sh
./scripts/sanity-check.sh
```
