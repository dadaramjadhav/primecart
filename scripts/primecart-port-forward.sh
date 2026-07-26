#!/usr/bin/env bash

set -euo pipefail

PIDS=()

cleanup() {
  echo
  echo "Stopping port-forwards..."

  for pid in "${PIDS[@]}"; do
    kill "$pid" 2>/dev/null || true
  done

  wait 2>/dev/null || true
}

trap cleanup EXIT INT TERM

forward() {
  local namespace="$1"
  local resource="$2"
  local ports="$3"
  local name="$4"

  kubectl port-forward \
    --namespace "$namespace" \
    "$resource" \
    "$ports" \
    >"/tmp/primecart-${name}.log" 2>&1 &

  PIDS+=("$!")

  echo "$name: $ports"
}

forward primecart-infra service/keycloak 8080:8080 keycloak
forward primecart-infra service/mysql 3307:3306 mysql
forward primecart-infra service/rabbitmq 5672:5672 rabbitmq
forward primecart-infra service/rabbitmq 15672:15672 rabbitmq-management
forward primecart-infra service/redis-insight 5540:5540 redis-insight
forward primecart-observe service/monitoring-kube-prometheus-prometheus 9090:9090 prometheus
forward primecart-observe service/monitoring-grafana 3000:80 grafana
forward primecart-app service/api-gateway 8181:8181 api-gateway
forward primecart-app service/primecart-ui 5173:80 primecart-ui
forward primecart-app service/sb-admin-server 9191:9191 sb-admin-server

echo
echo "PrimeCart port-forwards are running:"
echo "UI:                  http://localhost:5173"
echo "Gateway:             http://localhost:8181"
echo "Keycloak:            http://localhost:8080"
echo "MySQL:               127.0.0.1:3306"
echo "RabbitMQ:            127.0.0.1:5672"
echo "RabbitMQ Management: http://localhost:15672"
echo "Redis Insight:       http://localhost:5540"
echo "Prometheus:          http://localhost:9090"
echo "Grafana:             http://localhost:3000"
echo "Spring Boot Admin:   http://localhost:9191"
echo
echo "Press Ctrl+C to stop all port-forwards."

wait
