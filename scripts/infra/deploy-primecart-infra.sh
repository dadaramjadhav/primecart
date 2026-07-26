#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
TIMEOUT="${TIMEOUT:-15m}"

cd "${PROJECT_ROOT}"

echo "Creating PrimeCart namespaces..."
kubectl apply --filename k8s/namespaces/namespaces.yaml

echo "Checking required Secrets..."

REQUIRED_SECRETS=(
  "primecart-infra:mysql-credentials"
  "primecart-infra:redis-credentials"
  "primecart-infra:rabbitmq-credentials"
  "primecart-infra:keycloak-credentials"
  "primecart-observe:grafana-credentials"
  "primecart-observe:logstash-credentials"
)

for item in "${REQUIRED_SECRETS[@]}"; do
  namespace="${item%%:*}"
  secret="${item##*:}"

  if ! kubectl get secret "${secret}" \
    --namespace "${namespace}" >/dev/null 2>&1; then
    echo "Missing Secret: ${namespace}/${secret}"
    exit 1
  fi
done

echo "Updating Helm repositories..."

helm repo add bitnami https://charts.bitnami.com/bitnami --force-update
helm repo add prometheus-community \
  https://prometheus-community.github.io/helm-charts \
  --force-update
helm repo update

echo "Deploying infrastructure..."

kubectl apply --filename k8s/infrastructure/mysql/init-databases.yml

helm upgrade --install mysql bitnami/mysql \
  --namespace primecart-infra \
  --version 14.0.3 \
  --values k8s/infrastructure/mysql/values-local.yaml \
  --wait \
  --timeout "${TIMEOUT}"

kubectl apply --filename k8s/infrastructure/redis/redis.yml
kubectl apply --filename k8s/infrastructure/rabbitmq/rabbitmq.yml
kubectl apply --filename k8s/infrastructure/keycloak/keycloak.yml

kubectl rollout status statefulset/redis \
  --namespace primecart-infra --timeout="${TIMEOUT}"
kubectl rollout status deployment/redis-insight \
  --namespace primecart-infra --timeout="${TIMEOUT}"
kubectl rollout status statefulset/rabbitmq \
  --namespace primecart-infra --timeout="${TIMEOUT}"
kubectl rollout status deployment/keycloak \
  --namespace primecart-infra --timeout="${TIMEOUT}"

echo "Deploying observability..."

PROMETHEUS_CHART_VERSION="$(
  helm list --namespace primecart-observe --output json |
    jq -r '
      map(select(.name == "monitoring"))[0].chart // "" |
      sub("^kube-prometheus-stack-"; "")
    '
)"

if [[ -z "${PROMETHEUS_CHART_VERSION}" ]]; then
  PROMETHEUS_CHART_VERSION="$(
    helm search repo prometheus-community/kube-prometheus-stack \
      --versions --output json |
      jq -r '.[0].version'
  )"
fi

helm upgrade --install monitoring \
  prometheus-community/kube-prometheus-stack \
  --namespace primecart-observe \
  --version "${PROMETHEUS_CHART_VERSION}" \
  --values k8s/observability/prometheus/values-local.yaml \
  --wait \
  --timeout "${TIMEOUT}"

kubectl apply --filename k8s/observability/grafana/prometheus-datasource.yaml
kubectl apply --filename k8s/observability/tempo/tempo.yaml
kubectl rollout status deployment/tempo \
  --namespace primecart-observe --timeout="${TIMEOUT}"

kubectl apply --filename k8s/observability/otel-collector/otel-collector.yaml
kubectl rollout status deployment/otel-collector \
  --namespace primecart-observe --timeout="${TIMEOUT}"

kubectl apply --filename k8s/observability/logstash/logstash.yaml
kubectl rollout status deployment/logstash \
  --namespace primecart-observe --timeout="${TIMEOUT}"

echo "Deploying platform services..."

TIMEOUT="${TIMEOUT}" "${SCRIPT_DIR}/../platform/deploy-platform.sh"

echo "PrimeCart infrastructure, observability, and platform are ready."

kubectl get pods --namespace primecart-infra
kubectl get pods --namespace primecart-observe
kubectl get pods --namespace primecart-app
