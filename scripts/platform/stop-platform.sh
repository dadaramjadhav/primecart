#!/usr/bin/env bash

set -euo pipefail

kubectl scale deployment \
  config-server \
  sb-admin-server \
  --replicas=0 \
  --namespace primecart-app
