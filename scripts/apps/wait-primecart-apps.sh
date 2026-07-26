#!/usr/bin/env bash

set -uo pipefail

NAMESPACE="primecart-app"
TIMEOUT="${TIMEOUT:-10m}"
FAILED=()

APPLICATIONS=(
  api-gateway
  cart-service
  customer-service
  inventory-service
  order-service
  payment-service
  product-service
  primecart-ui
)

for application in "${APPLICATIONS[@]}"; do
  echo "Waiting for ${application}..."

  if ! kubectl rollout status "deployment/${application}" \
    --namespace "${NAMESPACE}" \
    --timeout="${TIMEOUT}"; then
    FAILED+=("${application}")
  fi
done

echo
kubectl get pods --namespace "${NAMESPACE}"

if ((${#FAILED[@]} > 0)); then
  echo
  echo "Failed applications:"
  printf ' - %s\n' "${FAILED[@]}"
  exit 1
fi

echo
echo "All PrimeCart applications are ready."
