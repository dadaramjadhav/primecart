#!/usr/bin/env bash

set -euo pipefail

for namespace in primecart-infra primecart-observe primecart-app; do
  echo
  echo "=== ${namespace} ==="

  kubectl get pods,deployments,services \
    --namespace "${namespace}"
done

echo
echo "=== Recent warnings ==="

kubectl get events \
  --all-namespaces \
  --field-selector type=Warning \
  --sort-by='.lastTimestamp' |
  tail -n 20
