#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
TIMEOUT="${TIMEOUT:-10m}"

echo "Installing ingress-nginx..."

helm upgrade --install ingress-nginx ingress-nginx \
  --repo https://kubernetes.github.io/ingress-nginx \
  --namespace ingress-nginx \
  --create-namespace \
  --wait \
  --timeout "${TIMEOUT}"

echo "Waiting for ingress-nginx controller..."

kubectl rollout status deployment/ingress-nginx-controller \
  --namespace ingress-nginx \
  --timeout="${TIMEOUT}"

echo "Applying PrimeCart local Ingress resources..."

kubectl apply \
  --kustomize "${PROJECT_ROOT}/k8s/overlays/local/ingress"

echo "Configured Ingress hosts:"

kubectl get ingress \
  --all-namespaces \
  --output custom-columns='NAMESPACE:.metadata.namespace,NAME:.metadata.name,HOST:.spec.rules[*].host,ADDRESS:.status.loadBalancer.ingress[*].ip'
