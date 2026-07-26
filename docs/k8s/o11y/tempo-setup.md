# Tempo on Docker Desktop Kubernetes

This guide installs Grafana Tempo for local PrimeCart distributed tracing.
Tempo runs in monolithic mode in the `primecart-observe` namespace and uses
persistent Docker Desktop storage.

## Prerequisites

- Docker Desktop Kubernetes is enabled.
- The current Kubernetes context is `docker-desktop`.
- `kubectl` is installed.
- The `primecart-observe` namespace exists.
- Grafana is running in `primecart-observe`.

Verify the prerequisites:

```bash
kubectl config current-context
kubectl get node
kubectl get namespace primecart-observe
kubectl get deployment monitoring-grafana \
  --namespace primecart-observe
```

## Kubernetes resources

The Tempo configuration is stored in:

```text
k8s/observability/tempo/
└── tempo.yaml
```

The manifest creates:

- A Tempo configuration `ConfigMap`
- A single-replica Tempo `Deployment`
- A ClusterIP `Service`
- A 5 Gi persistent volume
- OTLP gRPC ingestion on port `4317`
- OTLP HTTP ingestion on port `4318`
- Tempo HTTP and query access on port `3200`
- A Grafana Tempo data-source `ConfigMap`
- Readiness and liveness probes
- CPU and memory requests and limits

## Local Tempo architecture

Tempo runs as a single monolithic process. The default `all` target runs the
required components in one container and does not require Kafka.

Trace data is stored locally:

```text
WAL:    /var/tempo/wal
Blocks: /var/tempo/blocks
PVC:    tempo-data
Size:   5 Gi
```

This local filesystem backend is intended for development. Production
deployments should use reviewed immutable image versions and durable object
storage.

## Tempo 3 configuration

The current `grafana/tempo:latest` image uses the Tempo 3 configuration
schema. Tempo 3 removed the legacy `ingester` and `compactor` configuration
blocks.

The monolithic configuration therefore contains:

```yaml
server:
  http_listen_port: 3200

distributor:
  receivers:
    otlp:
      protocols:
        grpc:
          endpoint: 0.0.0.0:4317
        http:
          endpoint: 0.0.0.0:4318

storage:
  trace:
    backend: local
    wal:
      path: /var/tempo/wal
    local:
      path: /var/tempo/blocks
```

Tempo 3 migration details are available in the
[official Tempo upgrade guide](https://grafana.com/docs/tempo/latest/set-up-for-tracing/setup-tempo/upgrade/).

## Validate the manifest

Validate the manifest against the Kubernetes API without creating resources:

```bash
kubectl apply \
  --dry-run=server \
  --filename k8s/observability/tempo/tempo.yaml
```

## Install Tempo

```bash
kubectl apply \
  --filename k8s/observability/tempo/tempo.yaml
```

Wait for the deployment:

```bash
kubectl rollout status deployment/tempo \
  --namespace primecart-observe \
  --timeout 5m
```

## Verify the deployment

Check the pod and service:

```bash
kubectl get pods,services \
  --namespace primecart-observe \
  --selector app.kubernetes.io/name=tempo
```

Check persistent storage:

```bash
kubectl get pvc tempo-data \
  --namespace primecart-observe
```

Expected results:

```text
Tempo pod:   1/1 Running
tempo-data:  Bound
```

Inspect startup logs:

```bash
kubectl logs deployment/tempo \
  --namespace primecart-observe \
  --container tempo \
  --tail=100
```

## Verify Tempo readiness

Forward the Tempo HTTP service to the Mac:

```bash
kubectl port-forward \
  --namespace primecart-observe \
  service/tempo \
  3200:3200
```

Keep that terminal open and check readiness:

```bash
curl --fail http://127.0.0.1:3200/ready
```

Expected response:

```text
ready
```

Stopping the port-forward closes local Tempo HTTP access. It does not stop
Tempo inside Kubernetes.

## OTLP endpoints

The OpenTelemetry Collector will send traces to one of these addresses:

```text
OTLP gRPC:
tempo.primecart-observe.svc.cluster.local:4317

OTLP HTTP:
http://tempo.primecart-observe.svc.cluster.local:4318
```

Components in the `primecart-observe` namespace can use:

```text
tempo:4317
tempo:4318
```

Applications should normally send telemetry to the OpenTelemetry Collector.
The collector will then export traces to Tempo.

For temporary direct access from the Mac, forward the OTLP ports:

```bash
kubectl port-forward \
  --namespace primecart-observe \
  service/tempo \
  4317:4317 \
  4318:4318
```

## Grafana data source

The manifest creates:

```text
ConfigMap: grafana-tempo-datasource
Label:     grafana_datasource="1"
URL:       http://tempo:3200
UID:       tempo
```

The Grafana sidecar detects the labeled ConfigMap and provisions the Tempo
data source.

Restart Grafana if the data source is not detected:

```bash
kubectl rollout restart deployment/monitoring-grafana \
  --namespace primecart-observe
```

```bash
kubectl rollout status deployment/monitoring-grafana \
  --namespace primecart-observe \
  --timeout 5m
```

Forward Grafana:

```bash
kubectl port-forward \
  --namespace primecart-observe \
  service/monitoring-grafana \
  3000:80
```

Open the Grafana data-source page:

```text
http://localhost:3000/connections/datasources
```

Verify that `Tempo` appears and that its connection test succeeds.

## Troubleshooting

### Rollout times out

Check the pod:

```bash
kubectl get pods \
  --namespace primecart-observe \
  --selector app.kubernetes.io/name=tempo
```

Inspect the pod:

```bash
kubectl describe pod \
  --namespace primecart-observe \
  --selector app.kubernetes.io/name=tempo
```

Check events:

```bash
kubectl get events \
  --namespace primecart-observe \
  --sort-by=.lastTimestamp
```

Read Tempo logs:

```bash
kubectl logs deployment/tempo \
  --namespace primecart-observe \
  --container tempo \
  --tail=200
```

### Configuration parsing fails

This error indicates that a Tempo 2 configuration was used with Tempo 3:

```text
field ingester not found in type app.Config
field compactor not found in type app.Config
```

Remove these obsolete blocks:

```yaml
ingester:
  max_block_duration: 5m

compactor:
  compaction:
    block_retention: 24h
```

Apply the corrected manifest:

```bash
kubectl apply \
  --filename k8s/observability/tempo/tempo.yaml
```

Restart Tempo:

```bash
kubectl rollout restart deployment/tempo \
  --namespace primecart-observe
```

```bash
kubectl rollout status deployment/tempo \
  --namespace primecart-observe \
  --timeout 5m
```

### Persistent-volume permission errors

The manifest runs Tempo using UID and GID `10001` and configures:

```yaml
securityContext:
  fsGroup: 10001
```

Inspect the logs for permission errors:

```bash
kubectl logs deployment/tempo \
  --namespace primecart-observe \
  --container tempo
```

Check the PVC:

```bash
kubectl get pvc tempo-data \
  --namespace primecart-observe
```

### Tempo data source is missing in Grafana

Verify the data-source ConfigMap and label:

```bash
kubectl get configmap grafana-tempo-datasource \
  --namespace primecart-observe \
  --show-labels
```

Inspect the Grafana sidecar logs:

```bash
kubectl logs deployment/monitoring-grafana \
  --namespace primecart-observe \
  --container grafana-sc-datasources
```

Restart Grafana:

```bash
kubectl rollout restart deployment/monitoring-grafana \
  --namespace primecart-observe
```

### Inspect the active Tempo configuration

```bash
kubectl get configmap tempo-config \
  --namespace primecart-observe \
  --output yaml
```

```bash
kubectl exec \
  --namespace primecart-observe \
  deployment/tempo \
  -- cat /etc/tempo/tempo.yaml
```

### Restart Tempo without deleting traces

```bash
kubectl rollout restart deployment/tempo \
  --namespace primecart-observe
```

The deployment recreates the Tempo pod and reattaches the existing
`tempo-data` PVC.

Do not delete `tempo-data` unless intentionally deleting all locally stored
traces.

## Next step

Install the OpenTelemetry Collector:

```text
PrimeCart applications
        ↓ OTLP
OpenTelemetry Collector
        ↓ OTLP
Tempo
        ↓ queries
Grafana
```
