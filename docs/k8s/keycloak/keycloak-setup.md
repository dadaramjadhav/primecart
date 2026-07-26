# Keycloak on Docker Desktop Kubernetes

This guide installs Keycloak for local PrimeCart authentication and
authorization. Keycloak runs in the `primecart-infra` namespace and stores its
data in the existing Kubernetes MySQL instance.

## Prerequisites

- Docker Desktop Kubernetes is enabled.
- The current Kubernetes context is `docker-desktop`.
- `kubectl` is installed.
- The `primecart-infra` namespace exists.
- MySQL is running and ready.
- The `keycloak` MySQL database exists.
- The `primecart` MySQL user can access the `keycloak` database.

Verify the prerequisites:

```bash
kubectl config current-context
kubectl get node
kubectl get namespace primecart-infra
kubectl get pod mysql-0 --namespace primecart-infra
kubectl get service mysql --namespace primecart-infra
kubectl get secret mysql-credentials --namespace primecart-infra
```

## Kubernetes resources

The Keycloak configuration is stored in:

```text
k8s/infrastructure/keycloak/
└── keycloak.yml
```

The manifest creates:

- A single-replica Keycloak `Deployment`
- A ClusterIP `Service` on port `8080`
- MySQL database integration
- Kubernetes Secret references
- Startup, readiness, and liveness probes
- CPU and memory requests and limits

Keycloak is started using:

```text
start-dev
```

Development mode and the mutable `latest` image tag are acceptable for this
local environment. Pin the Keycloak image and use production mode, TLS, and
strict hostname validation before deploying to a shared or production
environment.

## Database configuration

Keycloak connects to:

```text
jdbc:mysql://mysql:3306/keycloak
```

Because Keycloak and MySQL run in the same `primecart-infra` namespace,
`mysql` resolves to the MySQL Kubernetes Service.

The equivalent fully qualified address is:

```text
mysql.primecart-infra.svc.cluster.local:3306
```

The database username is `primecart`. Its password is read from:

```text
Secret: mysql-credentials
Key:    mysql-password
```

## Create the Keycloak credentials

Enter the administrator password without placing it in a committed YAML file:

```bash
read -s "KEYCLOAK_ADMIN_PASSWORD?Keycloak admin password: "
echo
```

Create the Kubernetes Secret:

```bash
kubectl create secret generic keycloak-credentials \
  --namespace primecart-infra \
  --from-literal=admin-username=admin \
  --from-literal=admin-password="${KEYCLOAK_ADMIN_PASSWORD}"
```

Remove the password from the current shell:

```bash
unset KEYCLOAK_ADMIN_PASSWORD
```

Verify that the Secret exists without printing its contents:

```bash
kubectl get secret keycloak-credentials \
  --namespace primecart-infra
```

## Validate the manifest

Validate the manifest against the Kubernetes API without creating resources:

```bash
kubectl apply \
  --dry-run=server \
  --filename k8s/infrastructure/keycloak/keycloak.yml
```

## Install Keycloak

```bash
kubectl apply \
  --filename k8s/infrastructure/keycloak/keycloak.yml
```

Wait for the deployment:

```bash
kubectl rollout status deployment/keycloak \
  --namespace primecart-infra \
  --timeout 10m
```

## Verify the deployment

Check the pod and service:

```bash
kubectl get pods,services \
  --namespace primecart-infra \
  --selector app.kubernetes.io/name=keycloak
```

Expected pod status:

```text
keycloak   1/1   Running
```

Inspect startup logs:

```bash
kubectl logs deployment/keycloak \
  --namespace primecart-infra \
  --container keycloak \
  --tail=100
```

Check that Keycloak created its tables in MySQL:

```bash
MYSQL_VERIFY_PASSWORD="$(
  kubectl get secret mysql-credentials \
    --namespace primecart-infra \
    --output jsonpath='{.data.mysql-root-password}' |
  base64 --decode
)"
```

```bash
kubectl exec \
  --namespace primecart-infra \
  mysql-0 \
  -- env MYSQL_PWD="${MYSQL_VERIFY_PASSWORD}" \
  mysql \
    --user=root \
    --database=keycloak \
    --execute='SHOW TABLES;'
```

```bash
unset MYSQL_VERIFY_PASSWORD
```

## Access the administration console

Forward the Keycloak service to the Mac:

```bash
kubectl port-forward \
  --namespace primecart-infra \
  service/keycloak \
  8080:8080
```

Keep that terminal open and visit:

```text
http://localhost:8080
```

Retrieve the administrator username:

```bash
kubectl get secret keycloak-credentials \
  --namespace primecart-infra \
  --output jsonpath='{.data.admin-username}' |
base64 --decode
echo
```

Retrieve the administrator password:

```bash
kubectl get secret keycloak-credentials \
  --namespace primecart-infra \
  --output jsonpath='{.data.admin-password}' |
base64 --decode
echo
```

The default local login is:

```text
Username: admin
Password: value stored in keycloak-credentials
```

Stopping the port-forward closes local browser access. It does not stop
Keycloak inside Kubernetes.

## Verify OpenID Connect

With the port-forward running, verify the master realm discovery endpoint:

```bash
curl --fail \
  http://127.0.0.1:8080/realms/master/.well-known/openid-configuration
```

After creating or importing the PrimeCart realm, verify it with:

```bash
curl --fail \
  http://127.0.0.1:8080/realms/primecart/.well-known/openid-configuration
```

Applications inside Kubernetes access Keycloak through:

```text
http://keycloak.primecart-infra.svc.cluster.local:8080
```

Services in `primecart-app` can use:

```text
http://keycloak.primecart-infra:8080
```

The issuer URI for a `primecart` realm will be:

```text
http://keycloak.primecart-infra:8080/realms/primecart
```

Browser-facing applications may require an issuer URL based on
`http://localhost:8080`. Issuer consistency must be considered when moving
from port forwarding to an Ingress and stable local hostname.

## Management endpoints

The Keycloak container enables health and metrics:

```text
Container port: 9000
Health:         /health
Ready:          /health/ready
Live:           /health/live
Metrics:        /metrics
```

Forward the management port directly from the deployment:

```bash
kubectl port-forward \
  --namespace primecart-infra \
  deployment/keycloak \
  9000:9000
```

Verify health:

```bash
curl --fail http://127.0.0.1:9000/health
curl --fail http://127.0.0.1:9000/health/ready
curl --fail http://127.0.0.1:9000/health/live
```

View metrics:

```bash
curl --fail http://127.0.0.1:9000/metrics
```

## Troubleshooting

### Inspect status and events

```bash
kubectl get pods,services \
  --namespace primecart-infra \
  --selector app.kubernetes.io/name=keycloak
```

```bash
kubectl describe deployment keycloak \
  --namespace primecart-infra
```

```bash
kubectl describe pod \
  --namespace primecart-infra \
  --selector app.kubernetes.io/name=keycloak
```

```bash
kubectl get events \
  --namespace primecart-infra \
  --sort-by=.lastTimestamp
```

### Inspect logs

```bash
kubectl logs deployment/keycloak \
  --namespace primecart-infra \
  --container keycloak
```

Follow logs:

```bash
kubectl logs deployment/keycloak \
  --namespace primecart-infra \
  --container keycloak \
  --follow
```

### Keycloak cannot connect to MySQL

Verify MySQL DNS and port connectivity from the Keycloak pod:

```bash
kubectl exec \
  --namespace primecart-infra \
  deployment/keycloak \
  -- sh -c 'exec 3<>/dev/tcp/mysql/3306'
```

Verify the application database password:

```bash
MYSQL_APP_PASSWORD="$(
  kubectl get secret mysql-credentials \
    --namespace primecart-infra \
    --output jsonpath='{.data.mysql-password}' |
  base64 --decode
)"
```

```bash
kubectl exec \
  --namespace primecart-infra \
  mysql-0 \
  -- env MYSQL_PWD="${MYSQL_APP_PASSWORD}" \
  mysql \
    --user=primecart \
    --database=keycloak \
    --execute='SELECT 1;'
```

```bash
unset MYSQL_APP_PASSWORD
```

### Administrator login fails

The bootstrap administrator variables create an administrator only during
initial Keycloak database setup. Changing `keycloak-credentials` later does
not automatically update an existing administrator account stored in MySQL.

First verify the Secret values and inspect the Keycloak logs:

```bash
kubectl get secret keycloak-credentials \
  --namespace primecart-infra
```

```bash
kubectl logs deployment/keycloak \
  --namespace primecart-infra \
  --container keycloak \
  --tail=200
```

### Restart Keycloak

```bash
kubectl rollout restart deployment/keycloak \
  --namespace primecart-infra
```

```bash
kubectl rollout status deployment/keycloak \
  --namespace primecart-infra \
  --timeout 10m
```

Keycloak data remains in MySQL when the Keycloak pod is recreated.
