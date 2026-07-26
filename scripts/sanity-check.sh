#!/usr/bin/env bash

set -uo pipefail

FAILED=0

check_url() {
  local status

  status="$(
    curl --silent --output /dev/null \
      --write-out '%{http_code}' \
      --max-time 10 "$2" 2>/dev/null || true
  )"

  if [[ "${status}" =~ ^2[0-9][0-9]$ ]]; then
    printf '%-6s %-24s HTTP %s\n' "PASS" "$1" "${status}"
  else
    printf '%-6s %-24s HTTP %s\n' \
      "FAIL" "$1" "${status:-unreachable}"
    FAILED=1
  fi
}

check_service() {
  if kubectl get --raw \
    "/api/v1/namespaces/primecart-app/services/http:$1:http/proxy/actuator/health" \
    >/dev/null 2>&1; then
    printf '%-6s %-24s HTTP %s\n' "PASS" "$1" "200"
  else
    printf '%-6s %-24s HTTP %s\n' "FAIL" "$1" "unavailable"
    FAILED=1
  fi
}

echo "External endpoints:"

check_url "Keycloak" \
  "http://127.0.0.1:8080/realms/primecart/.well-known/openid-configuration"
check_url "API Gateway" "http://127.0.0.1:8181/actuator/health"
check_url "Prometheus" "http://127.0.0.1:9090/-/ready"
check_url "Grafana" "http://127.0.0.1:3000/api/health"
check_url "PrimeCart UI" "http://127.0.0.1:5173/"

echo
echo "Kubernetes health endpoints:"

for service in \
  config-server \
  product-service \
  cart-service \
  customer-service \
  inventory-service \
  order-service \
  payment-service; do
  check_service "${service}"
done

echo

if ((FAILED)); then
  echo "Sanity check failed."
  exit 1
fi

echo "All PrimeCart sanity checks passed."
