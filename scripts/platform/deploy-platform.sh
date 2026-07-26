#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

kubectl apply \
  --kustomize "${PROJECT_ROOT}/k8s/overlays/local/platform"

TIMEOUT="${TIMEOUT:-10m}" "${SCRIPT_DIR}/wait-platform.sh"
