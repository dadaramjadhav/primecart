# Logstash on Docker Desktop Kubernetes

This guide installs Logstash for local PrimeCart log processing. Logstash
receives JSON logs over TCP, enriches them, and forwards them to Splunk
through HTTP Event Collector (HEC).

Logstash runs in the `primecart-observe` namespace.

## Prerequisites

- Docker Desktop Kubernetes is enabled.
- The current Kubernetes context is `docker-desktop`.
- `kubectl` is installed.
- The `primecart-observe` namespace exists.
- Splunk is running in `primecart-observe`.
- Splunk HEC is enabled.
- The Splunk HEC token is available.

Verify the prerequisites:

```bash
kubectl config current-context
kubectl get node
kubectl get namespace primecart-observe
kubectl get pod splunk-0 --namespace primecart-observe
kubectl get service splunk --namespace primecart-observe
```

## Kubernetes resources

The Logstash configuration is stored in:

```text
k8s/observability/logstash/
└── logstash.yaml
```

The manifest creates:

- A Logstash configuration `ConfigMap`
- A single-replica Logstash `Deployment`
- A ClusterIP `Service`
- A 2 Gi persistent volume
- A persisted Logstash queue
- A dead-letter queue
- JSON TCP input on port `5000`
- Logstash monitoring API access on port `9600`
- Startup, readiness, and liveness probes
- CPU and memory requests and limits

## Log flow

```text
PrimeCart services
       │
       │ JSON lines over TCP :5000
       ▼
Logstash
       │
       │ HTTP Event Collector :8088
       ▼
Splunk
       │
       ▼
index=main
```

Applications send logs to:

```text
logstash.primecart-observe:5000
```

Logstash sends logs to:

```text
http://splunk:8088/services/collector/raw
```

Because Logstash and Splunk run in the same namespace, the short service name
`splunk` resolves through Kubernetes DNS.

## Pipeline configuration

### TCP input

The pipeline expects newline-delimited JSON:

```text
Port:  5000
Codec: json_lines
```

### Enrichment

Logstash adds:

```json
{
  "deployment_environment": "local-kubernetes"
}
```

### Splunk output

Logs are sent to:

```text
Index:      main
Sourcetype: primecart:json
Source:     value of the service field
```

The pipeline retries failed HEC requests and also writes events to standard
output using the Ruby debug codec.

## Persistent queue

The Logstash settings enable:

```yaml
queue.type: persisted
queue.max_bytes: 1gb
dead_letter_queue.enable: true
```

The queue and dead-letter data are stored on:

```text
PVC:  logstash-data
Size: 2 Gi
Path: /usr/share/logstash/data
```

This allows queued events to survive a Logstash pod restart.

## Create the Logstash credentials

Read the HEC token from the Splunk Secret:

```bash
SPLUNK_HEC_TOKEN="$(
  kubectl get secret splunk-credentials \
    --namespace primecart-observe \
    --output jsonpath='{.data.splunk-hec-token}' |
  base64 --decode
)"
```

Create the Logstash Secret:

```bash
kubectl create secret generic logstash-credentials \
  --namespace primecart-observe \
  --from-literal=splunk-hec-token="${SPLUNK_HEC_TOKEN}"
```

Remove the token from the current shell:

```bash
unset SPLUNK_HEC_TOKEN
```

Verify that the Secret exists without printing its contents:

```bash
kubectl get secret logstash-credentials \
  --namespace primecart-observe
```

The token stored in `logstash-credentials` must match the token configured in
Splunk.

## Validate the manifest

Validate the manifest against Kubernetes without creating resources:

```bash
kubectl apply \
  --dry-run=server \
  --filename k8s/observability/logstash/logstash.yaml
```

## Install Logstash

```bash
kubectl apply \
  --filename k8s/observability/logstash/logstash.yaml
```

Wait for the deployment:

```bash
kubectl rollout status deployment/logstash \
  --namespace primecart-observe \
  --timeout 10m
```

## Verify the deployment

Check the pod and service:

```bash
kubectl get pods,services \
  --namespace primecart-observe \
  --selector app.kubernetes.io/name=logstash
```

Check persistent storage:

```bash
kubectl get pvc logstash-data \
  --namespace primecart-observe
```

Expected results:

```text
Logstash pod: 1/1 Running
logstash-data: Bound
```

Inspect startup logs:

```bash
kubectl logs deployment/logstash \
  --namespace primecart-observe \
  --container logstash \
  --tail=200
```

## Verify the Logstash API

Forward the API port:

```bash
kubectl port-forward \
  --namespace primecart-observe \
  service/logstash \
  9600:9600
```

Keep that terminal open and verify the API:

```bash
curl --fail http://127.0.0.1:9600/
```

Check pipeline status:

```bash
curl --fail http://127.0.0.1:9600/_node/pipelines
```

Check pipeline statistics:

```bash
curl --fail http://127.0.0.1:9600/_node/stats/pipelines
```

Stopping the port-forward closes local API access. It does not stop Logstash.

## Test the TCP input

Forward local port `5001` to Logstash TCP port `5000`:

```bash
kubectl port-forward \
  --namespace primecart-observe \
  service/logstash \
  5001:5000
```

Keep that terminal open. In another terminal, send a JSON log:

```bash
printf '%s\n' \
  '{"service":"logstash-test","level":"INFO","message":"PrimeCart Kubernetes Logstash test"}' |
nc 127.0.0.1 5001
```

Verify that Logstash received the event:

```bash
kubectl logs deployment/logstash \
  --namespace primecart-observe \
  --container logstash \
  --tail=50
```

The debug output should include:

```text
service: logstash-test
deployment_environment: local-kubernetes
```

## Verify delivery to Splunk

Forward the Splunk web interface:

```bash
kubectl port-forward \
  --namespace primecart-observe \
  service/splunk \
  8000:8000
```

Open:

```text
http://localhost:8000
```

Search:

```text
index=main source="logstash-test"
```

Search by message:

```text
index=main "PrimeCart Kubernetes Logstash test"
```

## Application configuration

PrimeCart applications in `primecart-app` should use:

```text
logstash.primecart-observe:5000
```

The fully qualified address is:

```text
logstash.primecart-observe.svc.cluster.local:5000
```

Use an environment-driven Logback destination:

```xml
<destination>${LOGSTASH_DESTINATION:-localhost:5001}</destination>
```

Configure the Kubernetes Deployment:

```yaml
env:
  - name: LOGSTASH_DESTINATION
    value: logstash.primecart-observe:5000
```

Do not use `localhost` from an application pod. `localhost` refers to the
application container, not Logstash.

## Update the pipeline

After changing `logstash.conf` or `logstash.yml`, apply the manifest:

```bash
kubectl apply \
  --filename k8s/observability/logstash/logstash.yaml
```

Restart Logstash to load the updated ConfigMap:

```bash
kubectl rollout restart deployment/logstash \
  --namespace primecart-observe
```

```bash
kubectl rollout status deployment/logstash \
  --namespace primecart-observe \
  --timeout 10m
```

## Troubleshooting

### Rollout waits or times out

Check the pod:

```bash
kubectl get pods \
  --namespace primecart-observe \
  --selector app.kubernetes.io/name=logstash
```

Inspect the pod:

```bash
kubectl describe pod \
  --namespace primecart-observe \
  --selector app.kubernetes.io/name=logstash
```

Inspect recent events:

```bash
kubectl get events \
  --namespace primecart-observe \
  --sort-by=.lastTimestamp
```

Read Logstash logs:

```bash
kubectl logs deployment/logstash \
  --namespace primecart-observe \
  --container logstash \
  --tail=200
```

### Validate the pipeline syntax

```bash
kubectl exec \
  --namespace primecart-observe \
  deployment/logstash \
  -- /usr/share/logstash/bin/logstash \
    --path.settings /usr/share/logstash/config \
    --config.test_and_exit
```

Expected:

```text
Configuration OK
```

### Logstash cannot reach Splunk

Verify Splunk:

```bash
kubectl get pod,service \
  --namespace primecart-observe \
  --selector app.kubernetes.io/name=splunk
```

Verify that the Splunk Service exposes HEC port `8088`:

```bash
kubectl get service splunk \
  --namespace primecart-observe \
  --output jsonpath='{.spec.ports[?(@.name=="hec")].port}'
echo
```

Expected:

```text
8088
```

Inspect Logstash output errors:

```bash
kubectl logs deployment/logstash \
  --namespace primecart-observe \
  --container logstash |
rg --ignore-case 'splunk|hec|error|retry'
```

Verify that the manifest uses the Kubernetes service:

```text
http://splunk:8088/services/collector/raw
```

It should not use `host.docker.internal` after Splunk has moved into
Kubernetes.

### HEC authentication fails

Compare Secret hashes without printing the tokens:

```bash
kubectl get secret splunk-credentials \
  --namespace primecart-observe \
  --output jsonpath='{.data.splunk-hec-token}' |
shasum -a 256
```

```bash
kubectl get secret logstash-credentials \
  --namespace primecart-observe \
  --output jsonpath='{.data.splunk-hec-token}' |
shasum -a 256
```

The hashes must match.

After correcting the Logstash Secret, restart Logstash:

```bash
kubectl rollout restart deployment/logstash \
  --namespace primecart-observe
```

### Events appear in Logstash but not Splunk

Check the persistent queue statistics:

```bash
kubectl port-forward \
  --namespace primecart-observe \
  service/logstash \
  9600:9600
```

```bash
curl --fail \
  http://127.0.0.1:9600/_node/stats/pipelines |
jq
```

Check Splunk HEC health:

```bash
kubectl port-forward \
  --namespace primecart-observe \
  service/splunk \
  8088:8088
```

```bash
SPLUNK_HEC_TOKEN="$(
  kubectl get secret splunk-credentials \
    --namespace primecart-observe \
    --output jsonpath='{.data.splunk-hec-token}' |
  base64 --decode
)"
```

```bash
curl --fail \
  --header "Authorization: Splunk ${SPLUNK_HEC_TOKEN}" \
  http://127.0.0.1:8088/services/collector/health
```

```bash
unset SPLUNK_HEC_TOKEN
```

### Persistent queue permission errors

The container runs as UID and GID `1000`, and the pod configures:

```yaml
securityContext:
  fsGroup: 1000
```

Inspect Logstash logs and the PVC:

```bash
kubectl logs deployment/logstash \
  --namespace primecart-observe \
  --container logstash
```

```bash
kubectl get pvc logstash-data \
  --namespace primecart-observe
```

### Restart Logstash without deleting queued events

```bash
kubectl rollout restart deployment/logstash \
  --namespace primecart-observe
```

The deployment recreates the pod and reattaches `logstash-data`.

Do not delete the PVC unless intentionally deleting persisted queues and
dead-letter events.
