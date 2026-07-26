#!/usr/bin/env bash

set -euo pipefail

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

kubectl scale deployment \
  config-server \
  sb-admin-server \
  --replicas=0 \
  --namespace primecart-app

echo "PrimeCart infrastructure stopped. Persistent data is preserved."

kubectl get pods --namespace primecart-infra
kubectl get pods --namespace primecart-observe
kubectl get pods --namespace primecart-app
