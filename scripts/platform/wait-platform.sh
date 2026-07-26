#!/usr/bin/env bash

set -euo pipefail

TIMEOUT="${TIMEOUT:-10m}"

kubectl rollout status deployment/config-server \
  --namespace primecart-app \
  --timeout="${TIMEOUT}"

kubectl rollout status deployment/sb-admin-server \
  --namespace primecart-app \
  --timeout="${TIMEOUT}"
