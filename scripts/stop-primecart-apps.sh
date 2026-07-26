#!/usr/bin/env bash

set -euo pipefail

NAMESPACE="primecart-app"

echo "Stopping PrimeCart applications..."

kubectl scale deployment \
  api-gateway \
  cart-service \
  customer-service \
  inventory-service \
  order-service \
  payment-service \
  product-service \
  primecart-ui \
  --replicas=0 \
  --namespace "${NAMESPACE}"

echo "PrimeCart applications stopped."

kubectl get deployments \
  --namespace "${NAMESPACE}"