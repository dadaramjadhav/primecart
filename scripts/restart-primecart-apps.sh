#!/usr/bin/env bash

set -euo pipefail

echo "Restarting PrimeCart applications..."

kubectl rollout restart deployment \
  api-gateway \
  cart-service \
  customer-service \
  inventory-service \
  order-service \
  payment-service \
  product-service \
  primecart-ui \
  --namespace primecart-app

echo "Restart triggered. Current pod status:"

kubectl get pods \
  --namespace primecart-app
