# MySQL on Docker Desktop Kubernetes

This guide installs a single-node MySQL instance for local PrimeCart
development. MySQL runs in the `primecart-infra` namespace and uses persistent
Docker Desktop storage.

## Prerequisites

- Docker Desktop Kubernetes is enabled.
- The current Kubernetes context is `docker-desktop`.
- `kubectl` and Helm are installed.
- The `primecart-infra` namespace exists.

Verify the prerequisites:

```bash
kubectl config current-context
kubectl get node
kubectl get namespace primecart-infra
helm version
```

## Kubernetes resources

The MySQL configuration is stored in:

```text
k8s/infrastructure/mysql/
├── init-databases.yml
└── values-local.yaml
```

`init-databases.yml` creates the PrimeCart databases and grants access to the
`primecart` user. `values-local.yaml` configures a standalone MySQL instance,
a 5 Gi persistent volume, resource limits, health probes, and a ClusterIP
service.

## Add the Bitnami Helm repository

```bash
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update
```

Verify that the MySQL chart is available:

```bash
helm search repo bitnami/mysql --versions
```

The local values currently use:

```yaml
image:
  repository: bitnamilegacy/mysql
  tag: 9.4.0-debian-12-r1
```

The legacy repository is necessary because the image referenced by chart
`14.0.3` is no longer available from the free `bitnami/mysql` repository.
This is suitable for the current local environment, but it should be replaced
with a maintained image strategy before using this setup in production.

## Create the MySQL credentials

Enter the passwords without placing them in a committed YAML file:

```bash
read -s "MYSQL_ROOT_PASSWORD?MySQL root password: "
echo
read -s "MYSQL_APP_PASSWORD?PrimeCart MySQL password: "
echo
MYSQL_REPLICATION_PASSWORD="$(openssl rand -base64 32)"
```

Create the Kubernetes Secret:

```bash
kubectl create secret generic mysql-credentials \
  --namespace primecart-infra \
  --from-literal=mysql-root-password="${MYSQL_ROOT_PASSWORD}" \
  --from-literal=mysql-password="${MYSQL_APP_PASSWORD}" \
  --from-literal=mysql-replication-password="${MYSQL_REPLICATION_PASSWORD}"
```

Remove the password variables from the current shell:

```bash
unset MYSQL_ROOT_PASSWORD
unset MYSQL_APP_PASSWORD
unset MYSQL_REPLICATION_PASSWORD
```

Verify that the Secret exists without printing its contents:

```bash
kubectl get secret mysql-credentials \
  --namespace primecart-infra
```

## Create the database initializer

```bash
kubectl apply \
  --filename k8s/infrastructure/mysql/init-databases.yml
```

Verify the ConfigMap:

```bash
kubectl get configmap mysql-init-databases \
  --namespace primecart-infra
```

## Install MySQL

Install the pinned chart version:

```bash
helm upgrade --install mysql bitnami/mysql \
  --namespace primecart-infra \
  --version 14.0.3 \
  --values k8s/infrastructure/mysql/values-local.yaml \
  --wait \
  --timeout 10m
```

Check the release:

```bash
helm status mysql --namespace primecart-infra
```

## Verify MySQL

Check the pod, service, and persistent volume:

```bash
kubectl get pods,services,pvc \
  --namespace primecart-infra
```

Expected pod status:

```text
NAME      READY   STATUS    RESTARTS
mysql-0   1/1     Running   0
```

Read the root password into a temporary shell variable:

```bash
MYSQL_VERIFY_PASSWORD="$(
  kubectl get secret mysql-credentials \
    --namespace primecart-infra \
    --output jsonpath='{.data.mysql-root-password}' |
  base64 --decode
)"
```

Check server health:

```bash
kubectl exec \
  --namespace primecart-infra \
  mysql-0 \
  -- env MYSQL_PWD="${MYSQL_VERIFY_PASSWORD}" \
  mysqladmin ping --user=root
```

List the databases:

```bash
kubectl exec \
  --namespace primecart-infra \
  mysql-0 \
  -- env MYSQL_PWD="${MYSQL_VERIFY_PASSWORD}" \
  mysql --user=root --execute='SHOW DATABASES;'
```

Remove the temporary variable:

```bash
unset MYSQL_VERIFY_PASSWORD
```

PrimeCart creates these databases:

```text
cart
customer
inventory
keycloak
orders
payment
primecart
product
```

Applications inside Kubernetes connect to:

```text
mysql.primecart-infra.svc.cluster.local:3306
```

## Connect from MySQL Workbench

Forward local port `3307` to the MySQL Kubernetes service:

```bash
kubectl port-forward \
  --namespace primecart-infra \
  service/mysql \
  3307:3306
```

Keep that terminal open while using Workbench.

Retrieve the application password:

```bash
kubectl get secret mysql-credentials \
  --namespace primecart-infra \
  --output jsonpath='{.data.mysql-password}' |
base64 --decode
echo
```

Create a MySQL Workbench connection with:

```text
Connection Method: Standard TCP/IP
Hostname:          127.0.0.1
Port:              3307
Username:          primecart
Password:          application password retrieved above
```

Test the connection:

```sql
SHOW DATABASES;
```

Stopping the `kubectl port-forward` command closes local Workbench access. It
does not stop MySQL inside Kubernetes.

## Troubleshooting

### Helm waits and eventually times out

Inspect the pod and recent events:

```bash
kubectl get pod mysql-0 --namespace primecart-infra
kubectl describe pod mysql-0 --namespace primecart-infra
kubectl get events \
  --namespace primecart-infra \
  --sort-by=.lastTimestamp
```

Inspect MySQL logs:

```bash
kubectl logs mysql-0 \
  --namespace primecart-infra \
  --container mysql
```

Inspect the Helm release:

```bash
helm status mysql --namespace primecart-infra
helm history mysql --namespace primecart-infra
```

### ImagePullBackOff

Confirm which images the StatefulSet uses:

```bash
kubectl get statefulset mysql \
  --namespace primecart-infra \
  --output jsonpath='{.spec.template.spec.initContainers[0].image}{"\n"}{.spec.template.spec.containers[0].image}{"\n"}'
```

After correcting `values-local.yaml`, upgrade the release:

```bash
helm upgrade --install mysql bitnami/mysql \
  --namespace primecart-infra \
  --version 14.0.3 \
  --values k8s/infrastructure/mysql/values-local.yaml \
  --wait \
  --timeout 10m
```

If the StatefulSet template has the corrected image but the old unready pod
remains, recreate only the pod:

```bash
kubectl delete pod mysql-0 --namespace primecart-infra
```

The StatefulSet recreates the pod. The `data-mysql-0` PVC is not deleted.

### Check persistent storage

```bash
kubectl get pvc --namespace primecart-infra
```

Expected status:

```text
data-mysql-0   Bound
```

Do not delete the PVC unless intentionally deleting all local MySQL data.
