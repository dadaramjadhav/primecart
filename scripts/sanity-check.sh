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

check_internal_http() {
  if kubectl get --raw \
    "/api/v1/namespaces/$2/services/http:$3:$4/proxy$5" \
    >/dev/null 2>&1; then
    printf '%-6s %-24s HTTP %s\n' "PASS" "$1" "200"
  else
    printf '%-6s %-24s HTTP %s\n' "FAIL" "$1" "unavailable"
    FAILED=1
  fi
}

check_workload() {
  local desired
  local ready

  desired="$(
    kubectl get "$2/$3" \
      --namespace "$4" \
      --output jsonpath='{.spec.replicas}' 2>/dev/null || true
  )"

  ready="$(
    kubectl get "$2/$3" \
      --namespace "$4" \
      --output jsonpath='{.status.readyReplicas}' 2>/dev/null || true
  )"

  if [[ -n "${desired}" && "${desired}" != "0" && "${ready:-0}" == "${desired}" ]]; then
    printf '%-6s %-24s READY %s/%s\n' "PASS" "$1" "${ready}" "${desired}"
  else
    printf '%-6s %-24s READY %s/%s\n' \
      "FAIL" "$1" "${ready:-0}" "${desired:-0}"
    FAILED=1
  fi
}

echo "External endpoints:"

check_url "Keycloak" \
  "http://auth.primecart.localhost/realms/primecart/.well-known/openid-configuration"
check_url "API Gateway" "http://api.primecart.localhost/actuator/health"
check_url "Prometheus" "http://prometheus.primecart.localhost/-/ready"
check_url "Grafana" "http://grafana.primecart.localhost/api/health"
check_url "PrimeCart UI" "http://primecart.localhost/"

echo
echo "Infrastructure and observability:"

check_workload "MySQL" statefulset mysql primecart-infra
check_workload "Redis" statefulset redis primecart-infra
check_workload "RabbitMQ" statefulset rabbitmq primecart-infra

check_internal_http \
  "Tempo" primecart-observe tempo http /ready
check_internal_http \
  "OTel Collector" primecart-observe otel-collector health /
check_internal_http \
  "Logstash" primecart-observe logstash api /

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
