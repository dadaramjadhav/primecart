#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

echo "Deleting PrimeCart applications..."

kubectl delete \
  --kustomize "${PROJECT_ROOT}/k8s/overlays/local/applications" \
  --ignore-not-found=true

kubectl get deployments \
  --namespace primecart-app

echo "PrimeCart applications deleted."

kubectl get deployments \
  --namespace primecart-app
