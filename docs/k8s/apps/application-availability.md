# PrimeCart Application Availability

This guide documents the Kubernetes availability configuration used by the
PrimeCart application workloads.

## Scope

The configuration applies to these stateless applications in
`primecart-app`:

- API Gateway
- PrimeCart UI
- Product Service
- Cart Service
- Customer Service
- Inventory Service
- Order Service
- Payment Service

Their manifests are under:

```text
k8s/base/applications/
```

## Graceful Spring Boot shutdown

The common external Config Server `application.yml` contains:

```yaml
server:
  shutdown: graceful

spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s

management:
  endpoint:
    health:
      probes:
        enabled: true
```

Graceful shutdown allows active requests to finish before Spring Boot exits.
The probe setting exposes the Kubernetes liveness and readiness health groups.
Applications must restart after this common external configuration changes.

## Kubernetes health probes

The Spring Boot workloads use:

```yaml
startupProbe:
  httpGet:
    path: /actuator/health/liveness
    port: http
  initialDelaySeconds: 20
  periodSeconds: 10
  timeoutSeconds: 3
  failureThreshold: 30

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: http
  periodSeconds: 10
  timeoutSeconds: 3
  failureThreshold: 3

livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: http
  periodSeconds: 20
  timeoutSeconds: 3
  failureThreshold: 3
```

The startup probe gives the JVM time to initialize. The readiness probe
controls whether a Pod receives Service traffic. The liveness probe restarts a
stuck application.

The UI uses `/` for all three probes because NGINX serves the application:

```yaml
startupProbe:
  httpGet:
    path: /
    port: http
```

`port: http` refers to the named container port, not port 80 specifically.
Kubernetes resolves it to the `containerPort` declared with `name: http`.

## Termination handling

Each application container delays termination for ten seconds:

```yaml
lifecycle:
  preStop:
    exec:
      command: ["sh", "-c", "sleep 10"]
```

The Pod allows up to 45 seconds for termination:

```yaml
spec:
  template:
    spec:
      containers:
        - name: <application>
          lifecycle:
            # preStop configuration

      terminationGracePeriodSeconds: 45
```

`lifecycle` belongs inside the container. `terminationGracePeriodSeconds`
belongs in the Pod spec at the same level as `containers`.

The ten-second delay gives Kubernetes time to remove the terminating Pod from
Service endpoints. The remaining grace period allows Spring Boot to complete
its graceful shutdown.

## Replica counts and rolling updates

Each stateless application starts with two replicas:

```yaml
spec:
  replicas: 2
```

The Deployment strategy creates a replacement before removing an existing Pod:

```yaml
spec:
  replicas: 2
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 0
      maxSurge: 1
  minReadySeconds: 10
  progressDeadlineSeconds: 600
```

- `maxUnavailable: 0` keeps all desired replicas available during a rollout.
- `maxSurge: 1` permits one additional Pod while updating.
- `minReadySeconds: 10` requires a new Pod to remain ready before it is
  considered available.
- `progressDeadlineSeconds: 600` marks a stalled rollout as failed after ten
  minutes.

These fields belong in the Deployment `spec`, not `template.spec`.

## Pod disruption budgets

Each two-replica application has a matching PodDisruptionBudget:

```yaml
apiVersion: policy/v1
kind: PodDisruptionBudget
metadata:
  name: product-service
  namespace: primecart-app
spec:
  minAvailable: 1
  selector:
    matchLabels:
      app.kubernetes.io/name: product-service
```

The selector must exactly match the Deployment Pod label. A PDB protects
against voluntary disruptions such as node drains. It does not prevent
hardware failures, process crashes, or manual Pod deletion.

## Resource requests and limits

Java application containers currently use:

```yaml
resources:
  requests:
    cpu: 250m
    memory: 512Mi
  limits:
    cpu: "1"
    memory: 1Gi
```

The UI uses:

```yaml
resources:
  requests:
    cpu: 50m
    memory: 64Mi
  limits:
    cpu: 250m
    memory: 256Mi
```

CPU requests are required for CPU-utilization-based HPA calculations. These
values are starting points and should be adjusted using measured workload
usage.

## Horizontal Pod Autoscaling

Each application has an `autoscaling/v2` HPA:

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: product-service
  namespace: primecart-app
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: product-service
  minReplicas: 2
  maxReplicas: 5
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
```

The HPA maintains between two and five replicas and scales according to
average CPU utilization relative to the containers' CPU requests. Metrics
Server must be installed and healthy.

Verify it with:

```bash
kubectl get deployment metrics-server --namespace kube-system
kubectl get hpa --namespace primecart-app
kubectl top pods --namespace primecart-app
```

When an HPA manages a Deployment, avoid manually controlling its replica count.
Set the desired range through `minReplicas` and `maxReplicas`.

## Apply and verify

Apply the application Kustomize overlay used by the target environment, then
check the rollout:

```bash
kubectl get deployments --namespace primecart-app
kubectl get pods --namespace primecart-app
kubectl get poddisruptionbudgets --namespace primecart-app
kubectl get hpa --namespace primecart-app
```

Inspect one application:

```bash
kubectl rollout status deployment/product-service \
  --namespace primecart-app \
  --timeout=10m

kubectl describe deployment/product-service \
  --namespace primecart-app
```

During a rolling update, application traffic should continue while Kubernetes
starts a new Pod, waits for readiness, and then terminates an old Pod.

## Rollback

Review Deployment revisions:

```bash
kubectl rollout history deployment/product-service \
  --namespace primecart-app
```

Undo the latest rollout:

```bash
kubectl rollout undo deployment/product-service \
  --namespace primecart-app

kubectl rollout status deployment/product-service \
  --namespace primecart-app \
  --timeout=10m
```

Restore a specific revision when required:

```bash
kubectl rollout undo deployment/product-service \
  --namespace primecart-app \
  --to-revision=2
```
