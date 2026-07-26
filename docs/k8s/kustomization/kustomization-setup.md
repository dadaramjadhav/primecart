# PrimeCart Kustomize Setup

## Purpose

Kustomize manages the Kubernetes manifests for the PrimeCart backend
applications. The common application resources live in `k8s/base`, while the
local environment selects the Docker Hub image versions in
`k8s/overlays/local`.

Kustomize is built into `kubectl`, so a separate Kustomize installation is not
required.

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
└── overlays/
    └── local/
        └── applications/
            └── kustomization.yaml
```

Each backend base directory contains its Kubernetes manifest and a small
`kustomization.yaml`.

Example:

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization

resources:
  - product-service.yaml
```

## Base image configuration

The Deployment manifest contains the Docker Hub repository without an
environment-specific tag.

Example:

```yaml
containers:
  - name: product-service
    image: dadaramjadhav/primecart-product-service
    imagePullPolicy: IfNotPresent
```

The image name in the base manifest must exactly match the `name` in the
overlay. If the names differ, Kustomize will not replace the tag and Kubernetes
may try to pull `latest`.

## Local applications overlay

The local overlay includes all backend application bases:

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization

resources:
  - ../../../base/applications/api-gateway
  - ../../../base/applications/cart-service
  - ../../../base/applications/customer-service
  - ../../../base/applications/inventory-service
  - ../../../base/applications/order-service
  - ../../../base/applications/payment-service
  - ../../../base/applications/product-service
```

The `images` section assigns the immutable CI Git SHA tag:

```yaml
images:
  - name: dadaramjadhav/primecart-api-gateway
    newTag: d8f0ab7f5b31c2156fd8ac7a4b1274be3bfb011b

  - name: dadaramjadhav/primecart-product-service
    newTag: d8f0ab7f5b31c2156fd8ac7a4b1274be3bfb011b
```

When all service images are produced by the same successful CI commit, they
use the same Git SHA. Confirm that the tag exists in every Docker Hub
repository before deployment.

PrimeCart UI is currently applied separately because its directory does not
yet contain a Kustomize configuration and it is not listed in the local
overlay.

## Validate rendered manifests

Render the complete local overlay without changing the cluster:

```bash
kubectl kustomize \
  k8s/overlays/local/applications
```

Review the rendered image references:

```bash
kubectl kustomize \
  k8s/overlays/local/applications |
grep 'image:'
```

Every rendered backend image must contain the expected Docker Hub repository
and Git SHA tag.

## Update the application version

Update every overlay image tag using one full, 40-character CI commit SHA:

```bash
./scripts/apps/update-app-version.sh \
  d8f0ab7f5b31c2156fd8ac7a4b1274be3bfb011b
```

Review the change:

```bash
git diff -- \
  k8s/overlays/local/applications/kustomization.yaml
```

This command only updates the Kustomize file. It does not deploy anything.

## Deploy applications

Create or update all backend resources from the overlay and apply PrimeCart UI:

```bash
./scripts/apps/deploy-primecart-apps.sh
```

Equivalent commands:

```bash
kubectl apply \
  --kustomize k8s/overlays/local/applications

kubectl apply \
  --filename k8s/base/applications/primecart-ui/primecart-ui.yaml
```

Wait for every application:

```bash
./scripts/apps/wait-primecart-apps.sh
```

Use a longer timeout when Docker Desktop is pulling or emulating images:

```bash
TIMEOUT=15m ./scripts/apps/wait-primecart-apps.sh
```

## Restart applications

Restart the application pods without deleting Deployments or Services:

```bash
./scripts/apps/restart-primecart-apps.sh
```

A rollout restart preserves Services, so an active port-forward normally
continues working.

## Stop applications

Scale application Deployments to zero while keeping their Kubernetes
resources:

```bash
./scripts/apps/stop-primecart-apps.sh
```

Scaling to zero is preferred for routine local shutdown because Deployments,
Services, configuration, and port-forward targets remain defined.

Start the stopped Deployments again:

```bash
./scripts/apps/deploy-primecart-apps.sh
```

## Delete applications

Delete all application resources only when a clean recreation is required:

```bash
./scripts/apps/delete-primecart-apps.sh
```

Recreate them:

```bash
./scripts/apps/deploy-primecart-apps.sh
./scripts/apps/wait-primecart-apps.sh
```

Deleting Services terminates their existing `kubectl port-forward` processes.
After a delete and fresh deployment, restart port forwarding:

```bash
./scripts/primecart-port-forward.sh
```

An `ERR_CONNECTION_REFUSED` response from `http://localhost:8181` normally
means the API Gateway port-forward is not running.

## Check deployed images

```bash
kubectl get deployments \
  --namespace primecart-app \
  --output custom-columns='DEPLOYMENT:.metadata.name,IMAGE:.spec.template.spec.containers[*].image'
```

## Troubleshoot ImagePullBackOff

Check the rendered Kustomize images first:

```bash
kubectl kustomize \
  k8s/overlays/local/applications |
grep 'image:'
```

Inspect the failing pod:

```bash
kubectl get pods \
  --namespace primecart-app

kubectl describe pod <pod-name> \
  --namespace primecart-app
```

Common causes are:

- The overlay image `name` does not exactly match the base manifest image.
- The Git SHA tag does not exist in that service's Docker Hub repository.
- The Docker Hub repository is private and no image pull Secret is configured.
- The CI image architecture is incompatible with the Docker Desktop
  Kubernetes node.

Do not delete the Deployment merely to resolve `ImagePullBackOff`. Correct the
image name or tag, then apply the overlay again:

```bash
./scripts/apps/deploy-primecart-apps.sh
```

## Recommended deployment workflow

```bash
./scripts/apps/update-app-version.sh <40-character-git-sha>
kubectl kustomize k8s/overlays/local/applications
./scripts/apps/deploy-primecart-apps.sh
./scripts/apps/wait-primecart-apps.sh
./scripts/primecart-port-forward.sh
./scripts/sanity-check.sh
```
