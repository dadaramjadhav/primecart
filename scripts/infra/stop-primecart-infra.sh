#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "Stopping PrimeCart infrastructure..."

kubectl scale deployments,statefulsets \
  --all \
  --replicas=0 \
  --namespace primecart-infra

echo "Stopping PrimeCart observability..."

kubectl scale deployments,statefulsets \
  --all \
  --replicas=0 \
  --namespace primecart-observe

echo "Stopping PrimeCart platform services..."

"${SCRIPT_DIR}/../platform/stop-platform.sh"

echo "PrimeCart infrastructure stopped. Persistent data is preserved."

kubectl get pods --namespace primecart-infra
kubectl get pods --namespace primecart-observe
kubectl get pods --namespace primecart-app
