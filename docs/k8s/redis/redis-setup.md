# Redis and Redis Insight on Docker Desktop Kubernetes

This guide installs Redis and Redis Insight for local PrimeCart development.
Both components run in the `primecart-infra` namespace and use persistent
Docker Desktop storage.

## Prerequisites

- Docker Desktop Kubernetes is enabled.
- The current Kubernetes context is `docker-desktop`.
- `kubectl` is installed.
- The `primecart-infra` namespace exists.

Verify the prerequisites:

```bash
kubectl config current-context
kubectl get node
kubectl get namespace primecart-infra
```

## Kubernetes resources

The Redis configuration is stored in:

```text
k8s/infrastructure/redis/
└── redis.yml
```

The manifest creates:

- A Redis `StatefulSet`
- A Redis ClusterIP `Service`
- A 1 Gi Redis persistent volume
- A Redis Insight `Deployment`
- A Redis Insight ClusterIP `Service`
- A 1 Gi Redis Insight persistent volume
- Readiness and liveness probes
- CPU and memory requests and limits

Redis uses append-only-file persistence with `appendfsync everysec`.

## Create the Redis credentials

Enter the Redis password without placing it in a committed YAML file:

```bash
read -s "REDIS_PASSWORD?Redis password: "
echo
```

Create the Kubernetes Secret:

```bash
kubectl create secret generic redis-credentials \
  --namespace primecart-infra \
  --from-literal=redis-password="${REDIS_PASSWORD}"
```

Remove the password from the current shell:

```bash
unset REDIS_PASSWORD
```

Verify that the Secret exists without printing its contents:

```bash
kubectl get secret redis-credentials \
  --namespace primecart-infra
```

## Validate the manifest

Validate the manifest against the Kubernetes API without creating resources:

```bash
kubectl apply \
  --dry-run=server \
  --filename k8s/infrastructure/redis/redis.yml
```

## Install Redis and Redis Insight

```bash
kubectl apply \
  --filename k8s/infrastructure/redis/redis.yml
```

Wait for Redis:

```bash
kubectl rollout status statefulset/redis \
  --namespace primecart-infra \
  --timeout 5m
```

Wait for Redis Insight:

```bash
kubectl rollout status deployment/redis-insight \
  --namespace primecart-infra \
  --timeout 5m
```

## Verify the deployment

```bash
kubectl get pods,services,pvc \
  --namespace primecart-infra \
  --selector 'app.kubernetes.io/name in (redis,redis-insight)'
```

Expected pod status:

```text
redis-0          1/1   Running
redis-insight    1/1   Running
```

Expected persistent volume claims:

```text
redis-data-redis-0   Bound
redis-insight-data   Bound
```

## Test Redis authentication

Read the Redis password into a temporary shell variable:

```bash
REDIS_VERIFY_PASSWORD="$(
  kubectl get secret redis-credentials \
    --namespace primecart-infra \
    --output jsonpath='{.data.redis-password}' |
  base64 --decode
)"
```

Run `PING` inside the Redis pod:

```bash
kubectl exec \
  --namespace primecart-infra \
  redis-0 \
  -- env REDISCLI_AUTH="${REDIS_VERIFY_PASSWORD}" \
  redis-cli ping
```

Expected response:

```text
PONG
```

Remove the temporary variable:

```bash
unset REDIS_VERIFY_PASSWORD
```

Applications inside Kubernetes connect to:

```text
redis.primecart-infra.svc.cluster.local:6379
```

Services in the `primecart-infra` namespace can use the shorter address:

```text
redis:6379
```

## Access Redis Insight

Forward the Redis Insight service to the Mac:

```bash
kubectl port-forward \
  --namespace primecart-infra \
  service/redis-insight \
  5540:5540
```

Keep that terminal open and visit:

```text
http://localhost:5540
```

Retrieve the Redis password:

```bash
kubectl get secret redis-credentials \
  --namespace primecart-infra \
  --output jsonpath='{.data.redis-password}' |
base64 --decode
echo
```

Add the Redis database in Redis Insight with:

```text
Host:     redis
Port:     6379
Username: default
Password: password retrieved above
TLS:      Disabled
```

The equivalent connection URL is:

```text
redis://default:<password>@redis:6379
```

Do not use `127.0.0.1` as the Redis host in Redis Insight. Redis Insight runs
inside its own Kubernetes pod, so `127.0.0.1` refers to the Redis Insight
container rather than the Redis service.

## Connect to Redis directly from the Mac

Forward the Redis service:

```bash
kubectl port-forward \
  --namespace primecart-infra \
  service/redis \
  6379:6379
```

In another terminal, retrieve the password:

```bash
REDIS_LOCAL_PASSWORD="$(
  kubectl get secret redis-credentials \
    --namespace primecart-infra \
    --output jsonpath='{.data.redis-password}' |
  base64 --decode
)"
```

Connect with `redis-cli`:

```bash
REDISCLI_AUTH="${REDIS_LOCAL_PASSWORD}" \
  redis-cli --host 127.0.0.1 --port 6379
```

Remove the temporary variable after disconnecting:

```bash
unset REDIS_LOCAL_PASSWORD
```

The `127.0.0.1:6379` address works from the Mac only while the Redis
port-forward command is running.

## Troubleshooting

### Inspect status and events

```bash
kubectl get pods,services,pvc \
  --namespace primecart-infra \
  --selector 'app.kubernetes.io/name in (redis,redis-insight)'
```

```bash
kubectl get events \
  --namespace primecart-infra \
  --sort-by=.lastTimestamp
```

### Inspect Redis logs

```bash
kubectl logs redis-0 \
  --namespace primecart-infra \
  --container redis
```

### Inspect Redis Insight logs

```bash
kubectl logs deployment/redis-insight \
  --namespace primecart-infra \
  --container redis-insight
```

### Redis Insight cannot connect

Test Redis from inside the Redis Insight pod:

```bash
kubectl exec \
  --namespace primecart-infra \
  deployment/redis-insight \
  -- sh -c "nc -z redis 6379"
```

Confirm the Redis Insight connection uses `redis`, not `127.0.0.1`:

```text
redis://default:<password>@redis:6379
```

### Check persistent storage

```bash
kubectl get pvc \
  --namespace primecart-infra \
  --selector 'app.kubernetes.io/name in (redis,redis-insight)'
```

Do not delete `redis-data-redis-0` unless intentionally deleting all local
Redis data. Deleting `redis-insight-data` removes saved Redis Insight
connections and preferences.
