# Observability on Docker Desktop Kubernetes

This guide installs the initial PrimeCart observability stack using the
`kube-prometheus-stack` Helm chart. The stack runs in the
`primecart-observe` namespace.

The current phase includes:

- Prometheus
- Grafana
- Alertmanager
- Prometheus Operator
- kube-state-metrics
- Node Exporter

OpenTelemetry Collector and Tempo are not installed by this values file. They
will be added separately when application traces and OTLP collection are
configured.

## Prerequisites

- Docker Desktop Kubernetes is enabled.
- The current Kubernetes context is `docker-desktop`.
- `kubectl`, Helm, and `jq` are installed.
- The `primecart-observe` namespace exists.

Verify the prerequisites:

```bash
kubectl config current-context
kubectl get node
kubectl get namespace primecart-observe
helm version
jq --version
```

## Kubernetes configuration

The Helm values are stored in:

```text
k8s/observability/prometheus/
└── values-local.yaml
```

The local configuration provides:

```text
Prometheus retention:       7 days
Prometheus storage:         5 Gi
Grafana storage:            2 Gi
Alertmanager storage:       1 Gi
Storage class:              hostpath
Grafana service type:       ClusterIP
```

All storage is local to Docker Desktop Kubernetes.

## Add the Helm repository

```bash
helm repo add prometheus-community \
  https://prometheus-community.github.io/helm-charts
```

Update local Helm repository metadata:

```bash
helm repo update
```

Verify that the chart is available:

```bash
helm search repo \
  prometheus-community/kube-prometheus-stack \
  --versions
```

## Create Grafana credentials

Enter the Grafana administrator password without placing it in a committed
YAML file:

```bash
read -s "GRAFANA_ADMIN_PASSWORD?Grafana admin password: "
echo
```

Create the Kubernetes Secret:

```bash
kubectl create secret generic grafana-credentials \
  --namespace primecart-observe \
  --from-literal=admin-user=admin \
  --from-literal=admin-password="${GRAFANA_ADMIN_PASSWORD}"
```

Remove the password from the current shell:

```bash
unset GRAFANA_ADMIN_PASSWORD
```

Verify that the Secret exists without printing its contents:

```bash
kubectl get secret grafana-credentials \
  --namespace primecart-observe
```

## Select the Helm chart version

Read the latest available chart version into a shell variable:

```bash
PROMETHEUS_CHART_VERSION="$(
  helm search repo prometheus-community/kube-prometheus-stack \
    --versions \
    --output json |
  jq -r '.[0].version'
)"
```

Display the selected version:

```bash
echo "${PROMETHEUS_CHART_VERSION}"
```

Record this version when reproducible installations are required. Future
deployments should use an explicitly reviewed and pinned chart version rather
than automatically selecting a newer release.

## Validate the Helm configuration

Render the Kubernetes resources locally:

```bash
helm template monitoring \
  prometheus-community/kube-prometheus-stack \
  --namespace primecart-observe \
  --version "${PROMETHEUS_CHART_VERSION}" \
  --values k8s/observability/prometheus/values-local.yaml \
  > /tmp/primecart-monitoring-rendered.yaml
```

Validate the rendered resources against Kubernetes without creating them:

```bash
kubectl apply \
  --dry-run=server \
  --filename /tmp/primecart-monitoring-rendered.yaml
```

Remove the temporary rendered file:

```bash
rm /tmp/primecart-monitoring-rendered.yaml
```

## Install the observability stack

```bash
helm upgrade --install monitoring \
  prometheus-community/kube-prometheus-stack \
  --namespace primecart-observe \
  --version "${PROMETHEUS_CHART_VERSION}" \
  --values k8s/observability/prometheus/values-local.yaml \
  --wait \
  --timeout 15m
```

Remove the temporary chart-version variable:

```bash
unset PROMETHEUS_CHART_VERSION
```

## Verify the Helm release

```bash
helm status monitoring \
  --namespace primecart-observe
```

```bash
helm history monitoring \
  --namespace primecart-observe
```

## Verify Kubernetes resources

Check pods and services:

```bash
kubectl get pods,services \
  --namespace primecart-observe
```

Check persistent volumes:

```bash
kubectl get pvc \
  --namespace primecart-observe
```

Expected PVCs include storage for:

```text
Prometheus
Grafana
Alertmanager
```

Check the Prometheus custom resource:

```bash
kubectl get prometheus \
  --namespace primecart-observe
```

Check Alertmanager:

```bash
kubectl get alertmanager \
  --namespace primecart-observe
```

Check ServiceMonitor resources:

```bash
kubectl get servicemonitors \
  --namespace primecart-observe
```

Check PrometheusRule resources:

```bash
kubectl get prometheusrules \
  --namespace primecart-observe
```

Check all deployments and StatefulSets:

```bash
kubectl get deployments,statefulsets \
  --namespace primecart-observe
```

## Access Prometheus

Forward Prometheus to the Mac:

```bash
kubectl port-forward \
  --namespace primecart-observe \
  service/monitoring-kube-prometheus-prometheus \
  9090:9090
```

Keep that terminal open and visit:

```text
http://localhost:9090
```

Verify readiness from another terminal:

```bash
curl --fail http://127.0.0.1:9090/-/ready
```

Verify health:

```bash
curl --fail http://127.0.0.1:9090/-/healthy
```

View discovered targets:

```text
http://localhost:9090/targets
```

## Access Grafana

Forward Grafana to the Mac:

```bash
kubectl port-forward \
  --namespace primecart-observe \
  service/monitoring-grafana \
  3000:80
```

Keep that terminal open and visit:

```text
http://localhost:3000
```

Retrieve the administrator username:

```bash
kubectl get secret grafana-credentials \
  --namespace primecart-observe \
  --output jsonpath='{.data.admin-user}' |
base64 --decode
echo
```

Retrieve the administrator password:

```bash
kubectl get secret grafana-credentials \
  --namespace primecart-observe \
  --output jsonpath='{.data.admin-password}' |
base64 --decode
echo
```

The default local login is:

```text
Username: admin
Password: value stored in grafana-credentials
```

Verify Grafana health:

```bash
curl --fail http://127.0.0.1:3000/api/health
```

The Helm chart automatically provisions Prometheus as a Grafana data source.

## Access Alertmanager

Forward Alertmanager to the Mac:

```bash
kubectl port-forward \
  --namespace primecart-observe \
  service/monitoring-kube-prometheus-alertmanager \
  9093:9093
```

Keep that terminal open and visit:

```text
http://localhost:9093
```

Verify readiness:

```bash
curl --fail http://127.0.0.1:9093/-/ready
```

## Internal service addresses

Applications and monitoring resources inside Kubernetes can use:

```text
Prometheus:
monitoring-kube-prometheus-prometheus.primecart-observe.svc.cluster.local:9090

Grafana:
monitoring-grafana.primecart-observe.svc.cluster.local:80

Alertmanager:
monitoring-kube-prometheus-alertmanager.primecart-observe.svc.cluster.local:9093
```

These ClusterIP addresses are not directly accessible from the Mac. Use
port-forwarding until an Ingress is configured.

## Monitor PrimeCart services

Each PrimeCart service should expose Spring Boot Actuator metrics:

```text
/actuator/prometheus
```

The Kubernetes Service for each application should contain a named metrics
port, for example:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: product-service
  namespace: primecart-app
  labels:
    app.kubernetes.io/name: product-service
spec:
  selector:
    app.kubernetes.io/name: product-service
  ports:
    - name: http
      port: 8080
      targetPort: http
```

A `ServiceMonitor` can then select that service:

```yaml
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: product-service
  namespace: primecart-observe
  labels:
    release: monitoring
spec:
  namespaceSelector:
    matchNames:
      - primecart-app
  selector:
    matchLabels:
      app.kubernetes.io/name: product-service
  endpoints:
    - port: http
      path: /actuator/prometheus
      interval: 30s
```

Create application `ServiceMonitor` resources when the corresponding
applications are deployed.

## Troubleshooting

### Helm installation waits or times out

Inspect the release:

```bash
helm status monitoring \
  --namespace primecart-observe
```

Inspect pods:

```bash
kubectl get pods \
  --namespace primecart-observe \
  --output wide
```

Inspect events:

```bash
kubectl get events \
  --namespace primecart-observe \
  --sort-by=.lastTimestamp
```

Inspect persistent volume claims:

```bash
kubectl get pvc \
  --namespace primecart-observe
```

### Inspect component logs

Prometheus Operator:

```bash
kubectl logs deployment/monitoring-kube-prometheus-operator \
  --namespace primecart-observe
```

Grafana:

```bash
kubectl logs deployment/monitoring-grafana \
  --namespace primecart-observe
```

Prometheus:

```bash
kubectl logs \
  --namespace primecart-observe \
  prometheus-monitoring-kube-prometheus-prometheus-0 \
  --container prometheus
```

Alertmanager:

```bash
kubectl logs \
  --namespace primecart-observe \
  alertmanager-monitoring-kube-prometheus-alertmanager-0 \
  --container alertmanager
```

### Grafana login fails

Verify that the expected Secret exists:

```bash
kubectl get secret grafana-credentials \
  --namespace primecart-observe
```

Restart Grafana after correcting its Secret:

```bash
kubectl rollout restart deployment/monitoring-grafana \
  --namespace primecart-observe
```

```bash
kubectl rollout status deployment/monitoring-grafana \
  --namespace primecart-observe \
  --timeout 5m
```

### Prometheus does not discover an application

Check the ServiceMonitor:

```bash
kubectl get servicemonitor \
  --namespace primecart-observe
```

Check that its labels match the Prometheus selector:

```bash
kubectl get prometheus \
  --namespace primecart-observe \
  --output yaml
```

Check the application Service labels and port names:

```bash
kubectl get service \
  --namespace primecart-app \
  --show-labels
```

Check the Prometheus targets page:

```text
http://localhost:9090/targets
```

### Check persistent storage

```bash
kubectl get pvc \
  --namespace primecart-observe
```

Do not delete observability PVCs unless intentionally deleting local
Prometheus history, Grafana state, or Alertmanager state.

## Upgrade the stack

Update Helm repositories:

```bash
helm repo update
```

Select and review the intended chart version:

```bash
helm search repo \
  prometheus-community/kube-prometheus-stack \
  --versions
```

Upgrade using an explicit version:

```bash
helm upgrade monitoring \
  prometheus-community/kube-prometheus-stack \
  --namespace primecart-observe \
  --version <reviewed-chart-version> \
  --values k8s/observability/prometheus/values-local.yaml \
  --wait \
  --timeout 15m
```

## Next observability components

Install these after the metrics stack is healthy:

```text
1. Tempo for trace storage
2. OpenTelemetry Collector for OTLP ingestion
3. Grafana Tempo data source
4. PrimeCart application metrics ServiceMonitors
5. PrimeCart application dashboards and alerts
```
