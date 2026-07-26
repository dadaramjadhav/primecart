# OpenTelemetry Collector on Docker Desktop Kubernetes

This guide installs the OpenTelemetry Collector for local PrimeCart
observability. The collector receives OTLP telemetry from PrimeCart services,
exports traces to Tempo, and exposes received metrics for Prometheus.

The collector runs in the `primecart-observe` namespace.

## Prerequisites

- Docker Desktop Kubernetes is enabled.
- The current Kubernetes context is `docker-desktop`.
- `kubectl` is installed.
- The `primecart-observe` namespace exists.
- Prometheus is running in `primecart-observe`.
- Tempo is running and ready in `primecart-observe`.
- The Prometheus Operator CRDs are installed.

Verify the prerequisites:

```bash
kubectl config current-context
kubectl get node
kubectl get namespace primecart-observe
kubectl get prometheus --namespace primecart-observe
kubectl get deployment tempo --namespace primecart-observe
kubectl get customresourcedefinition servicemonitors.monitoring.coreos.com
```

## Kubernetes resources

The collector configuration is stored in:

```text
k8s/observability/otel-collector/
└── otel-collector.yaml
```

The manifest creates:

- A collector configuration `ConfigMap`
- A single-replica collector `Deployment`
- A ClusterIP `Service`
- A Prometheus `ServiceMonitor`
- OTLP gRPC ingestion on port `4317`
- OTLP HTTP ingestion on port `4318`
- Health checks on port `13133`
- Collector internal metrics on port `8888`
- Received application metrics on port `8889`
- Startup, readiness, and liveness probes
- CPU and memory requests and limits

The current local manifest uses:

```text
otel/opentelemetry-collector-contrib:latest
```

The mutable `latest` tag is convenient for local development. Use an
explicitly reviewed and pinned image version before deploying to a shared or
production environment.

## Telemetry flow

```text
PrimeCart services
       │
       │ OTLP gRPC :4317 or OTLP HTTP :4318
       ▼
OpenTelemetry Collector
       ├── traces ──► Tempo :4317
       ├── metrics ─► Prometheus exporter :8889
       └── logs ────► debug exporter
```

The logs pipeline currently writes received logs to the collector output
through the debug exporter. It does not persist logs. Add Loki or another log
backend before relying on OTLP log storage.

## Collector configuration

### Receivers

The OTLP receiver accepts:

```text
gRPC: 0.0.0.0:4317
HTTP: 0.0.0.0:4318
```

### Processors

The memory limiter protects the collector from exceeding its configured
memory limit:

```yaml
memory_limiter:
  check_interval: 1s
  limit_mib: 384
  spike_limit_mib: 64
```

The batch processor groups telemetry before exporting it:

```yaml
batch:
  timeout: 5s
  send_batch_size: 1024
```

### Exporters

Traces are sent to Tempo using:

```text
tempo:4317
```

TLS is disabled because this is an internal local-cluster connection.

Received metrics are exposed in Prometheus format:

```text
0.0.0.0:8889/metrics
```

Collector internal metrics are exposed at:

```text
0.0.0.0:8888/metrics
```

## Validate the manifest

Validate the manifest against the Kubernetes API without creating resources:

```bash
kubectl apply \
  --dry-run=server \
  --filename k8s/observability/otel-collector/otel-collector.yaml
```

## Install the collector

```bash
kubectl apply \
  --filename k8s/observability/otel-collector/otel-collector.yaml
```

Wait for the deployment:

```bash
kubectl rollout status deployment/otel-collector \
  --namespace primecart-observe \
  --timeout 5m
```

## Verify the deployment

Check the pod and service:

```bash
kubectl get pods,services \
  --namespace primecart-observe \
  --selector app.kubernetes.io/name=otel-collector
```

Expected pod status:

```text
otel-collector   1/1   Running
```

Check the ServiceMonitor:

```bash
kubectl get servicemonitor otel-collector \
  --namespace primecart-observe
```

Inspect collector startup logs:

```bash
kubectl logs deployment/otel-collector \
  --namespace primecart-observe \
  --container otel-collector \
  --tail=100
```

## Verify health

Forward the health endpoint:

```bash
kubectl port-forward \
  --namespace primecart-observe \
  service/otel-collector \
  13133:13133
```

Keep that terminal open and verify health:

```bash
curl --fail http://127.0.0.1:13133/
```

Stopping the port-forward closes local access. It does not stop the collector.

## Verify Prometheus metrics

Forward both metrics ports:

```bash
kubectl port-forward \
  --namespace primecart-observe \
  service/otel-collector \
  8888:8888 \
  8889:8889
```

Check collector internal metrics:

```bash
curl --fail http://127.0.0.1:8888/metrics
```

Check metrics received through OTLP:

```bash
curl --fail http://127.0.0.1:8889/metrics
```

Port `8889` may contain few or no application metrics until a PrimeCart
service exports OTLP metrics.

## Verify Prometheus discovery

Forward Prometheus:

```bash
kubectl port-forward \
  --namespace primecart-observe \
  service/monitoring-kube-prometheus-prometheus \
  9090:9090
```

Open the targets page:

```text
http://localhost:9090/targets
```

Verify that both OpenTelemetry Collector targets are healthy:

```text
telemetry port:  8888
prometheus port: 8889
```

Query collector metrics:

```text
http://localhost:9090/graph
```

Example PromQL:

```promql
up{service="otel-collector"}
```

## Application configuration

PrimeCart applications in the `primecart-app` namespace should use the
cross-namespace collector service:

```text
OTLP HTTP:
http://otel-collector.primecart-observe:4318

OTLP gRPC:
http://otel-collector.primecart-observe:4317
```

The fully qualified addresses are:

```text
otel-collector.primecart-observe.svc.cluster.local:4317
otel-collector.primecart-observe.svc.cluster.local:4318
```

Recommended environment variables for OTLP HTTP:

```yaml
env:
  - name: OTEL_EXPORTER_OTLP_ENDPOINT
    value: http://otel-collector.primecart-observe:4318

  - name: OTEL_EXPORTER_OTLP_PROTOCOL
    value: http/protobuf

  - name: OTEL_RESOURCE_ATTRIBUTES
    value: deployment.environment=local
```

Set the service name separately for every application:

```yaml
env:
  - name: OTEL_SERVICE_NAME
    value: product-service
```

Do not use `localhost` as the collector host from an application pod.
`localhost` refers to that application pod, not the collector.

## Send a local OTLP request

Forward the OTLP HTTP endpoint:

```bash
kubectl port-forward \
  --namespace primecart-observe \
  service/otel-collector \
  4318:4318
```

The local OTLP HTTP base address is:

```text
http://127.0.0.1:4318
```

OTLP signal endpoints are:

```text
Traces:  http://127.0.0.1:4318/v1/traces
Metrics: http://127.0.0.1:4318/v1/metrics
Logs:    http://127.0.0.1:4318/v1/logs
```

These endpoints expect protobuf or OTLP JSON payloads, not ordinary browser
requests.

## Verify trace export to Tempo

Check the collector logs for exporter errors:

```bash
kubectl logs deployment/otel-collector \
  --namespace primecart-observe \
  --container otel-collector \
  --tail=200
```

Check Tempo logs:

```bash
kubectl logs deployment/tempo \
  --namespace primecart-observe \
  --container tempo \
  --tail=200
```

After an application sends traces, open Grafana:

```bash
kubectl port-forward \
  --namespace primecart-observe \
  service/monitoring-grafana \
  3000:80
```

Navigate to:

```text
Explore → Tempo
```

Search by the application's `service.name`.

## Update the collector configuration

After modifying the ConfigMap configuration, apply it:

```bash
kubectl apply \
  --filename k8s/observability/otel-collector/otel-collector.yaml
```

Restart the collector so it reloads the mounted configuration:

```bash
kubectl rollout restart deployment/otel-collector \
  --namespace primecart-observe
```

```bash
kubectl rollout status deployment/otel-collector \
  --namespace primecart-observe \
  --timeout 5m
```

## Troubleshooting

### Rollout waits or times out

Check the pod:

```bash
kubectl get pods \
  --namespace primecart-observe \
  --selector app.kubernetes.io/name=otel-collector
```

Inspect the pod:

```bash
kubectl describe pod \
  --namespace primecart-observe \
  --selector app.kubernetes.io/name=otel-collector
```

Inspect recent events:

```bash
kubectl get events \
  --namespace primecart-observe \
  --sort-by=.lastTimestamp
```

Read the logs:

```bash
kubectl logs deployment/otel-collector \
  --namespace primecart-observe \
  --container otel-collector \
  --tail=200
```

### Configuration parsing fails

Inspect the active configuration:

```bash
kubectl get configmap otel-collector-config \
  --namespace primecart-observe \
  --output yaml
```

```bash
kubectl exec \
  --namespace primecart-observe \
  deployment/otel-collector \
  -- cat /etc/otelcol-contrib/config.yaml
```

Because the manifest uses a mutable collector image, a future image may
introduce configuration changes. Pin the last working image version before
upgrading configuration.

### Collector cannot reach Tempo

Verify Tempo:

```bash
kubectl get pod,service \
  --namespace primecart-observe \
  --selector app.kubernetes.io/name=tempo
```

Inspect collector logs for OTLP exporter errors:

```bash
kubectl logs deployment/otel-collector \
  --namespace primecart-observe \
  --container otel-collector |
rg --ignore-case tempo
```

Verify the Tempo OTLP gRPC port:

```bash
kubectl get service tempo \
  --namespace primecart-observe \
  --output jsonpath='{.spec.ports[?(@.name=="otlp-grpc")].port}'
echo
```

Expected:

```text
4317
```

### Prometheus does not discover the collector

Verify the ServiceMonitor label:

```bash
kubectl get servicemonitor otel-collector \
  --namespace primecart-observe \
  --show-labels
```

Expected label:

```text
release=monitoring
```

Verify the service labels and named ports:

```bash
kubectl get service otel-collector \
  --namespace primecart-observe \
  --output yaml
```

Verify the Prometheus targets:

```text
http://localhost:9090/targets
```

### Application telemetry does not arrive

Verify the application configuration:

```text
OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector.primecart-observe:4318
OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf
```

Check the application logs for exporter failures.

Check the collector logs:

```bash
kubectl logs deployment/otel-collector \
  --namespace primecart-observe \
  --container otel-collector \
  --follow
```

### Restart the collector

```bash
kubectl rollout restart deployment/otel-collector \
  --namespace primecart-observe
```

The collector is stateless. Restarting it does not delete traces already
stored in Tempo or metrics already stored in Prometheus.
