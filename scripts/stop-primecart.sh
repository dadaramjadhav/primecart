#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "Stopping PrimeCart applications..."
"${SCRIPT_DIR}/apps/stop-primecart-apps.sh"

echo "Stopping PrimeCart infrastructure, observability and platform..."
"${SCRIPT_DIR}/infra/stop-primecart-infra.sh"

echo "Stopping Splunk..."
docker compose \
  --file "${SCRIPT_DIR}/../devops/splunk/docker-compose-splunk.yml" \
  stop

echo "PrimeCart stopped. Persistent data is preserved."
"${SCRIPT_DIR}/status-primecart.sh"