#!/usr/bin/env bash

set -euo pipefail

kubectl rollout restart deployment \
  config-server \
  sb-admin-server \
  --namespace primecart-app

kubectl get pods \
  --namespace primecart-app \
  --selector 'app.kubernetes.io/name in (config-server,sb-admin-server)'
