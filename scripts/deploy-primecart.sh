#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TIMEOUT="${TIMEOUT:-15m}"

echo "Starting Splunk..."
docker compose \
  --file "${SCRIPT_DIR}/../devops/splunk/docker-compose-splunk.yml" \
  up --detach

echo "Verifying Docker images..."
"${SCRIPT_DIR}/verify-images.sh"

echo "Deploying PrimeCart infrastructure, observability and platform..."
TIMEOUT="${TIMEOUT}" \
  "${SCRIPT_DIR}/infra/deploy-primecart-infra.sh"

echo "Deploying PrimeCart applications..."
"${SCRIPT_DIR}/apps/deploy-primecart-apps.sh"

echo "Waiting for PrimeCart applications..."
TIMEOUT="${TIMEOUT}" \
  "${SCRIPT_DIR}/apps/wait-primecart-apps.sh"

echo "Deploying PrimeCart Ingress..."
TIMEOUT="${TIMEOUT}" \
  "${SCRIPT_DIR}/ingress/deploy-ingress.sh"

echo "PrimeCart deployment completed."
"${SCRIPT_DIR}/status-primecart.sh"
