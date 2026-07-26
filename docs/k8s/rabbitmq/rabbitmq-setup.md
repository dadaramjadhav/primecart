# RabbitMQ on Docker Desktop Kubernetes

This guide installs a single-node RabbitMQ broker for local PrimeCart
development. RabbitMQ runs in the `primecart-infra` namespace and uses
persistent Docker Desktop storage.

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

The RabbitMQ configuration is stored in:

```text
k8s/infrastructure/rabbitmq/
└── rabbitmq.yml
```

The manifest creates:

- A single-replica RabbitMQ `StatefulSet`
- A ClusterIP `Service`
- A 2 Gi persistent volume
- AMQP access on port `5672`
- Management UI access on port `15672`
- Startup, readiness, and liveness probes
- CPU and memory requests and limits

The local environment uses the `rabbitmq:4-management` image.

## Create the RabbitMQ credentials

Enter the administrator password without placing it in a committed YAML file:

```bash
read -s "RABBITMQ_PASSWORD?RabbitMQ password: "
echo
RABBITMQ_ERLANG_COOKIE="$(openssl rand -hex 32)"
```

Create the Kubernetes Secret:

```bash
kubectl create secret generic rabbitmq-credentials \
  --namespace primecart-infra \
  --from-literal=rabbitmq-username=admin \
  --from-literal=rabbitmq-password="${RABBITMQ_PASSWORD}" \
  --from-literal=rabbitmq-erlang-cookie="${RABBITMQ_ERLANG_COOKIE}"
```

Remove the credentials from the current shell:

```bash
unset RABBITMQ_PASSWORD
unset RABBITMQ_ERLANG_COOKIE
```

Verify that the Secret exists without printing its contents:

```bash
kubectl get secret rabbitmq-credentials \
  --namespace primecart-infra
```

## Validate the manifest

Validate the manifest against the Kubernetes API without creating resources:

```bash
kubectl apply \
  --dry-run=server \
  --filename k8s/infrastructure/rabbitmq/rabbitmq.yml
```

## Install RabbitMQ

```bash
kubectl apply \
  --filename k8s/infrastructure/rabbitmq/rabbitmq.yml
```

Wait for RabbitMQ:

```bash
kubectl rollout status statefulset/rabbitmq \
  --namespace primecart-infra \
  --timeout 5m
```

## Verify the deployment

Check the pod, service, and persistent volume:

```bash
kubectl get pods,services \
  --namespace primecart-infra \
  --selector app.kubernetes.io/name=rabbitmq
```

```bash
kubectl get pvc \
  --namespace primecart-infra
```

Expected pod status:

```text
rabbitmq-0   1/1   Running
```

Expected persistent volume claim:

```text
rabbitmq-data-rabbitmq-0   Bound
```

Check that the RabbitMQ node responds:

```bash
kubectl exec \
  --namespace primecart-infra \
  rabbitmq-0 \
  -- rabbitmq-diagnostics ping
```

Expected response:

```text
Ping succeeded
```

Check that the RabbitMQ application is running:

```bash
kubectl exec \
  --namespace primecart-infra \
  rabbitmq-0 \
  -- rabbitmq-diagnostics check_running
```

Display the broker status:

```bash
kubectl exec \
  --namespace primecart-infra \
  rabbitmq-0 \
  -- rabbitmqctl status
```

List users:

```bash
kubectl exec \
  --namespace primecart-infra \
  rabbitmq-0 \
  -- rabbitmqctl list_users
```

## Access the management UI

Forward the RabbitMQ management service to the Mac:

```bash
kubectl port-forward \
  --namespace primecart-infra \
  service/rabbitmq \
  15672:15672
```

Keep that terminal open and visit:

```text
http://localhost:15672
```

Retrieve the username:

```bash
kubectl get secret rabbitmq-credentials \
  --namespace primecart-infra \
  --output jsonpath='{.data.rabbitmq-username}' |
base64 --decode
echo
```

Retrieve the password:

```bash
kubectl get secret rabbitmq-credentials \
  --namespace primecart-infra \
  --output jsonpath='{.data.rabbitmq-password}' |
base64 --decode
echo
```

The default local login is:

```text
Username: admin
Password: value stored in rabbitmq-credentials
```

Stopping the port-forward closes local management access. It does not stop
RabbitMQ inside Kubernetes.

## Application connection

Applications inside Kubernetes connect to:

```text
rabbitmq.primecart-infra.svc.cluster.local:5672
```

Services in the `primecart-infra` namespace can use:

```text
rabbitmq:5672
```

Services in `primecart-app` should use the fully qualified service address or
the shorter cross-namespace address:

```text
rabbitmq.primecart-infra:5672
```

The Spring configuration will eventually use values equivalent to:

```yaml
spring:
  rabbitmq:
    host: rabbitmq.primecart-infra
    port: 5672
    username: admin
    password: ${RABBITMQ_PASSWORD}
```

The password must come from a Kubernetes Secret or Vault, not from a committed
application configuration file.

## Connect from the Mac

Forward the AMQP port:

```bash
kubectl port-forward \
  --namespace primecart-infra \
  service/rabbitmq \
  5672:5672
```

Local clients can then use:

```text
amqp://admin:<password>@127.0.0.1:5672
```

The local address works only while the AMQP port-forward command is running.

## Verify the management API

Read the credentials into temporary shell variables:

```bash
RABBITMQ_VERIFY_USERNAME="$(
  kubectl get secret rabbitmq-credentials \
    --namespace primecart-infra \
    --output jsonpath='{.data.rabbitmq-username}' |
  base64 --decode
)"
```

```bash
RABBITMQ_VERIFY_PASSWORD="$(
  kubectl get secret rabbitmq-credentials \
    --namespace primecart-infra \
    --output jsonpath='{.data.rabbitmq-password}' |
  base64 --decode
)"
```

With the management port-forward running, check broker health:

```bash
curl \
  --user "${RABBITMQ_VERIFY_USERNAME}:${RABBITMQ_VERIFY_PASSWORD}" \
  http://127.0.0.1:15672/api/health/checks/alarms
```

Remove the variables:

```bash
unset RABBITMQ_VERIFY_USERNAME
unset RABBITMQ_VERIFY_PASSWORD
```

## Troubleshooting

### Inspect status and events

```bash
kubectl get pods,services \
  --namespace primecart-infra \
  --selector app.kubernetes.io/name=rabbitmq
```

```bash
kubectl get pvc \
  --namespace primecart-infra
```

```bash
kubectl describe pod rabbitmq-0 \
  --namespace primecart-infra
```

```bash
kubectl get events \
  --namespace primecart-infra \
  --sort-by=.lastTimestamp
```

### Inspect RabbitMQ logs

```bash
kubectl logs rabbitmq-0 \
  --namespace primecart-infra \
  --container rabbitmq
```

Follow logs:

```bash
kubectl logs rabbitmq-0 \
  --namespace primecart-infra \
  --container rabbitmq \
  --follow
```

### Management login fails

Verify the configured users:

```bash
kubectl exec \
  --namespace primecart-infra \
  rabbitmq-0 \
  -- rabbitmqctl list_users
```

The `RABBITMQ_DEFAULT_USER` and `RABBITMQ_DEFAULT_PASS` variables create the
initial user only when RabbitMQ initializes an empty data directory. Changing
the Kubernetes Secret later does not automatically change a user stored in the
existing RabbitMQ database.

To update the existing administrator password without deleting data:

```bash
RABBITMQ_NEW_PASSWORD="$(
  kubectl get secret rabbitmq-credentials \
    --namespace primecart-infra \
    --output jsonpath='{.data.rabbitmq-password}' |
  base64 --decode
)"
```

```bash
kubectl exec \
  --namespace primecart-infra \
  rabbitmq-0 \
  -- rabbitmqctl change_password admin "${RABBITMQ_NEW_PASSWORD}"
```

```bash
unset RABBITMQ_NEW_PASSWORD
```

### Check persistent storage

```bash
kubectl get pvc \
  --namespace primecart-infra
```

Do not delete `rabbitmq-data-rabbitmq-0` unless intentionally deleting all
local queues, exchanges, bindings, users, and messages.

### Restart the pod without deleting data

```bash
kubectl delete pod rabbitmq-0 \
  --namespace primecart-infra
```

The StatefulSet recreates the pod and reattaches the existing persistent
volume.
