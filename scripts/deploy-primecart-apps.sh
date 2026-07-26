#!/usr/bin/env bash

#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

echo "Deploying PrimeCart applications..."

kubectl apply \
  --kustomize "${PROJECT_ROOT}/k8s/overlays/local/applications"

kubectl get deployments,pods \
  --namespace primecart-app

echo "PrimeCart applications deployed."
