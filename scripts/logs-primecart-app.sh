#!/usr/bin/env bash

set -euo pipefail

SERVICE="${1:-}"
NAMESPACE="primecart-app"
TAIL="${TAIL:-200}"

if [[ -z "${SERVICE}" ]]; then
  echo "Usage: $0 <service>"
  echo "Example: $0 payment-service"
  exit 1
fi

if ! kubectl get deployment "${SERVICE}" \
  --namespace "${NAMESPACE}" >/dev/null 2>&1; then
  echo "Deployment not found: ${SERVICE}"
  exit 1
fi

kubectl logs "deployment/${SERVICE}" \
  --namespace "${NAMESPACE}" \
  --all-containers=true \
  --tail="${TAIL}" \
  --follow
