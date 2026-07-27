# PrimeCart Kubernetes Resource Management

This guide documents how CPU and memory are measured, requested, limited, deployed, and troubleshot in the PrimeCart Kubernetes environment.

## Why resource management is required

Kubernetes uses resource requests to decide where a Pod can run. Resource limits prevent one container from consuming an uncontrolled amount of node capacity.

```text
Request → capacity reserved for scheduling
Limit   → maximum resource usage allowed
Usage   → current consumption reported by Metrics Server
```

Requests and limits are configured for each container, not for the Deployment as a whole.

## CPU units

Kubernetes expresses CPU in cores or millicores:

```text
1000m = 1 CPU core
250m  = 0.25 CPU core
100m  = 0.10 CPU core
```

For example:

```yaml
resources:
  requests:
    cpu: 250m
  limits:
    cpu: "1"
```

Kubernetes reserves `250m` for scheduling, while the container may use up to one CPU core. A container attempting to exceed its CPU limit is throttled; it is not terminated solely for exceeding CPU.

## Memory units

Kubernetes commonly expresses memory in mebibytes and gibibytes:

```text
1024Mi = 1Gi
```

For example:

```yaml
resources:
  requests:
    memory: 512Mi
  limits:
    memory: 1Gi
```

Kubernetes uses the request when scheduling the Pod. The limit applies to the container's total memory. A container that exceeds its memory limit may be terminated with the reason `OOMKilled`.

## Container memory and JVM memory

A Kubernetes memory value is the total container memory, not only the Java heap:

```text
JVM heap
+ Metaspace
+ code cache
+ thread stacks
+ direct buffers
+ native libraries and JVM runtime
= total container memory
```

Do not configure the Java maximum heap equal to the container limit. The JVM needs memory outside the heap.

Modern Java versions are container-aware and calculate a suitable heap from the container limit. Inspect the calculated value with:

```bash
kubectl exec \
  --namespace primecart-app \
  deployment/product-service \
  -- java -XshowSettings:vm -version 2>&1 |
grep -i "Max. Heap Size"
```

If explicit JVM control becomes necessary, use percentage-based settings:

```yaml
env:
  - name: JAVA_TOOL_OPTIONS
    value: >-
      -XX:InitialRAMPercentage=25
      -XX:MaxRAMPercentage=65
```

For a `1Gi` container limit, a 65% maximum heap leaves approximately 35% for non-heap and native memory.

## Current PrimeCart application resources

The Java business services currently use:

```yaml
resources:
  requests:
    cpu: 250m
    memory: 512Mi
  limits:
    cpu: "1"
    memory: 1Gi
```

This configuration is present in:

- API Gateway
- Product Service
- Cart Service
- Customer Service
- Inventory Service
- Order Service
- Payment Service

The PrimeCart UI uses fewer resources because its container serves static files through NGINX:

```yaml
resources:
  requests:
    cpu: 50m
    memory: 64Mi
  limits:
    cpu: 250m
    memory: 256Mi
```

Config Server and Spring Boot Admin currently use:

```yaml
resources:
  requests:
    cpu: 200m
    memory: 384Mi
  limits:
    cpu: "1"
    memory: 768Mi
```

These are starting values for the local environment. Production values must be based on measurements taken under representative traffic.

## Install Metrics Server

The Metrics Server provides the Kubernetes Metrics API used by `kubectl top`.

Check whether it is available:

```bash
kubectl get deployment metrics-server \
  --namespace kube-system
```

```bash
kubectl get apiservice v1beta1.metrics.k8s.io
```

Install it with Helm:

```bash
helm repo add metrics-server \
  https://kubernetes-sigs.github.io/metrics-server/
```

```bash
helm repo update
```

Docker Desktop commonly requires insecure kubelet TLS for this local component:

```bash
helm upgrade --install metrics-server \
  metrics-server/metrics-server \
  --namespace kube-system \
  --set 'args[0]=--kubelet-insecure-tls'
```

Wait for the deployment:

```bash
kubectl rollout status deployment/metrics-server \
  --namespace kube-system \
  --timeout=5m
```

Confirm the API is available:

```bash
kubectl get apiservice v1beta1.metrics.k8s.io
```

The `AVAILABLE` value should be `True`. Allow approximately 30–60 seconds for initial metrics collection.

> `--kubelet-insecure-tls` is suitable for this local Docker Desktop environment. Do not use it as a default production configuration.

## Measure current usage

Display node usage:

```bash
kubectl top nodes
```

Display application Pods ordered by CPU:

```bash
kubectl top pods \
  --namespace primecart-app \
  --sort-by=cpu
```

Display application Pods ordered by memory:

```bash
kubectl top pods \
  --namespace primecart-app \
  --sort-by=memory
```

Display all namespaces:

```bash
kubectl top pods \
  --all-namespaces \
  --sort-by=memory
```

`kubectl top` shows recent usage. It does not show requests and limits, and one idle measurement is not enough for production sizing.

## Inspect configured resources

Show resource blocks and their surrounding values:

```bash
rg -n \
  --context 5 \
  "resources:" \
  k8s/base/applications \
  k8s/platform
```

Show the resources currently applied to Payment Service:

```bash
kubectl get deployment payment-service \
  --namespace primecart-app \
  --output jsonpath='{.spec.template.spec.containers[0].resources}' |
jq
```

Show resources for every application container:

```bash
kubectl get deployments \
  --namespace primecart-app \
  --output json |
jq -r '
  .items[] |
  .metadata.name as $deployment |
  .spec.template.spec.containers[] |
  [
    $deployment,
    .name,
    (.resources.requests.cpu // "-"),
    (.resources.requests.memory // "-"),
    (.resources.limits.cpu // "-"),
    (.resources.limits.memory // "-")
  ] |
  @tsv
'
```

## Apply resource changes

Render the local Kustomize overlay:

```bash
kubectl kustomize \
  k8s/overlays/local \
  >/tmp/primecart-local.yaml
```

Validate the rendered resources against the cluster:

```bash
kubectl apply \
  --dry-run=server \
  --filename /tmp/primecart-local.yaml
```

Apply the overlay:

```bash
kubectl apply \
  --kustomize k8s/overlays/local
```

Resource changes modify the Pod template. Kubernetes therefore creates a new Deployment revision and replaces the Pods through a rolling update.

Wait for application and platform rollouts:

```bash
./scripts/apps/wait-primecart-apps.sh
```

```bash
./scripts/platform/wait-platform.sh
```

## Validate after deployment

Check the Pods:

```bash
kubectl get pods \
  --namespace primecart-app
```

Check recent resource usage:

```bash
kubectl top pods \
  --namespace primecart-app \
  --sort-by=memory
```

Check node allocation:

```bash
kubectl describe nodes |
sed -n '/Allocated resources:/,/Events:/p'
```

Run application health checks:

```bash
./scripts/sanity-check.sh
```

## Troubleshooting

### Metrics API not available

```text
error: Metrics API not available
```

Check the Metrics Server:

```bash
kubectl get pods \
  --namespace kube-system \
  --selector app.kubernetes.io/name=metrics-server
```

```bash
kubectl logs deployment/metrics-server \
  --namespace kube-system \
  --tail=200
```

```bash
kubectl describe apiservice v1beta1.metrics.k8s.io
```

### Pod is OOMKilled

Check the previous container state:

```bash
kubectl get pods \
  --namespace primecart-app \
  --output jsonpath='{range .items[*]}{.metadata.name}{"\t"}{.status.containerStatuses[0].lastState.terminated.reason}{"\n"}{end}'
```

Inspect one Pod:

```bash
kubectl describe pod <pod-name> \
  --namespace primecart-app
```

If the reason is `OOMKilled`, inspect JVM heap configuration and measured usage before increasing the memory limit.

### Pod remains Pending

```bash
kubectl describe pod <pod-name> \
  --namespace primecart-app
```

```bash
kubectl get events \
  --namespace primecart-app \
  --sort-by=.lastTimestamp |
tail -n 30
```

A message such as `Insufficient memory` or `Insufficient cpu` means the scheduler cannot find enough unreserved node capacity for the Pod's requests.

### Application is slow

Check usage:

```bash
kubectl top pod <pod-name> \
  --namespace primecart-app
```

Check whether the CPU limit is too low, whether the JVM is performing frequent garbage collection, and whether downstream services are slow. CPU throttling can increase latency even when a Pod remains healthy.

## Sizing guidelines

1. Measure startup, idle, normal-load, and peak-load usage.
2. Set memory requests near stable normal usage.
3. Leave enough memory headroom for JVM startup and traffic spikes.
4. Keep memory limits above observed peak total container usage.
5. Set CPU requests to the service's normal requirement.
6. Set CPU limits high enough to allow startup and short bursts.
7. Load-test before choosing production values.
8. Revisit values after application or dependency upgrades.

Resource configuration is an iterative operational decision. It should be based on measurements, not copied unchanged between unrelated services or environments.
