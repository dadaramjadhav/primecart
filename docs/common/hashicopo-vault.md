# HashiCorp Vault Setup for PrimeCart

This guide documents the local HashiCorp Vault setup used by PrimeCart. It covers persistent Raft storage, KV version 2 secrets, a least-privilege policy, AppRole authentication, and the Product Service integration through Spring Cloud Vault.

> This is a local learning setup. TLS and auto-unseal are not configured. Never commit root tokens, unseal keys, Role IDs, Secret IDs, or application passwords.

## Architecture

```text
Product Service
  │
  │ VAULT_ROLE_ID + VAULT_SECRET_ID
  ▼
Vault AppRole login
  │
  │ restricted application token
  ▼
primecart-common-read policy
  │
  │ read only
  ▼
kv/primecart/common
  │
  ├── SBA_USERNAME
  └── SBA_PASSWORD
```

The setup consists of five layers:

1. Vault server with persistent Raft storage.
2. KV version 2 secret engine mounted at `kv/`.
3. Read-only ACL policy named `primecart-common-read`.
4. AppRole named `product-service`.
5. Spring Cloud Vault integration in Product Service.

## 1. Configure persistent Vault storage

Create `infra/vault/vault-config.hcl`:

```hcl
ui = true
disable_mlock = true

storage "raft" {
  path    = "/vault/data"
  node_id = "primecart-vault-1"
}

listener "tcp" {
  address     = "0.0.0.0:8200"
  tls_disable = true
}

api_addr     = "http://localhost:8200"
cluster_addr = "http://primecart-vault:8201"
```

`disable_mlock = true` is appropriate for this local learning environment. In production, disable swap or use encrypted swap before disabling `mlock`.

## 2. Configure Docker Compose

Create `infra/vault/docker-compose-vault.yml`:

```yaml
services:
  vault-init:
    image: hashicorp/vault:2.0.3
    user: "0:0"
    command: ["sh", "-c", "chown -R 100:1000 /vault/data"]
    volumes:
      - vault-data:/vault/data

  vault:
    image: hashicorp/vault:2.0.3
    container_name: primecart-vault

    depends_on:
      vault-init:
        condition: service_completed_successfully

    ports:
      - "8200:8200"

    environment:
      VAULT_ADDR: http://127.0.0.1:8200

    command: server

    volumes:
      - vault-data:/vault/data
      - ./vault-config.hcl:/vault/config/vault.hcl:ro

    restart: unless-stopped

volumes:
  vault-data:
```

The official image entrypoint automatically adds `-config=/vault/config`. Keep the command as `server`; adding another `-config` option can load the listener twice and cause `address already in use` errors.

The `vault-init` service assigns the named volume to Vault's container user (`100:1000`). This prevents `permission denied` errors when Vault creates `/vault/data/vault.db`.

Pin the Vault image version rather than using `latest` so upgrades are intentional.

## 3. Start Vault

From the repository root:

```bash
docker compose \
  -f infra/vault/docker-compose-vault.yml \
  up -d
```

Check the container:

```bash
docker compose \
  -f infra/vault/docker-compose-vault.yml \
  ps
```

Check the logs:

```bash
docker compose \
  -f infra/vault/docker-compose-vault.yml \
  logs --tail=100 vault
```

Expected startup details include:

```text
Storage: raft
Mlock: supported: true, enabled: false
Vault server started
```

## 4. Initialize the Raft cluster

Open the Vault UI:

```text
http://localhost:8200
```

Choose **Create a new Raft cluster**.

For this learning environment, configure:

```text
Key shares:    1
Key threshold: 1
```

Save the generated unseal key and initial root token in a password manager. Do not store them in this repository.

For production, use multiple key shares and an appropriate threshold.

## 5. Unseal Vault

Enter the unseal key in the UI, or run:

```bash
docker compose \
  -f infra/vault/docker-compose-vault.yml \
  exec \
  -e VAULT_ADDR=http://127.0.0.1:8200 \
  vault vault operator unseal YOUR_UNSEAL_KEY
```

Vault data survives restarts, but Vault starts sealed after each restart. Manual unsealing is required until auto-unseal is configured.

Check status:

```bash
docker compose \
  -f infra/vault/docker-compose-vault.yml \
  exec \
  -e VAULT_ADDR=http://127.0.0.1:8200 \
  vault vault status
```

Expected:

```text
Initialized    true
Sealed         false
Storage Type   raft
```

## 6. Enable the KV version 2 secrets engine

In the Vault UI:

1. Open **Secrets Engines**.
2. Click **Enable new engine**.
3. Select **KV**.
4. Select **KV version 2**.
5. Set the mount path to `kv`.
6. Enable the engine.

The mount path is important. This project uses `kv/`, not `secret/`.

## 7. Store the Spring Boot Admin credentials

Inside the `kv/` engine, create a secret with this path:

```text
primecart/common
```

Add these keys:

```text
SBA_USERNAME
SBA_PASSWORD
```

Do not include `kv/` in the UI secret path because the UI is already inside that engine.

The resulting logical path is:

```text
kv/primecart/common
```

The KV version 2 API path is:

```text
kv/data/primecart/common
```

Verify with the root token:

```bash
export VAULT_ROOT_TOKEN='YOUR_INITIAL_ROOT_TOKEN'

docker compose \
  -f infra/vault/docker-compose-vault.yml \
  exec \
  -e VAULT_ADDR=http://127.0.0.1:8200 \
  -e VAULT_TOKEN="$VAULT_ROOT_TOKEN" \
  vault vault kv get kv/primecart/common
```

## 8. Create a read-only policy

In the Vault UI:

1. Open **Policies**.
2. Open **ACL policies**.
3. Create a policy named `primecart-common-read`.
4. Use the Policy Rules editor or Visual Editor.

Policy rules:

```hcl
path "kv/data/primecart/common" {
  capabilities = ["read"]
}

path "kv/metadata/primecart/common" {
  capabilities = ["read"]
}
```

When using the Visual Editor, create two entries:

| Path | Capability |
|---|---|
| `kv/data/primecart/common` | `read` |
| `kv/metadata/primecart/common` | `read` |

Do not grant `update`, `delete`, `patch`, `list`, or `sudo`.

## 9. Enable AppRole authentication

In the Vault UI:

1. Open **Access**.
2. Open **Authentication methods**.
3. Click **Enable new method**.
4. Select **AppRole**.
5. Use the path `approle`.
6. Enable the method.

Confirm through the CLI:

```bash
docker compose \
  -f infra/vault/docker-compose-vault.yml \
  exec \
  -e VAULT_ADDR=http://127.0.0.1:8200 \
  -e VAULT_TOKEN="$VAULT_ROOT_TOKEN" \
  vault vault auth list
```

Expected:

```text
approle/    approle
```

## 10. Create the Product Service AppRole

The Vault UI may not provide AppRole role management. Create the role through the CLI:

```bash
docker compose \
  -f infra/vault/docker-compose-vault.yml \
  exec \
  -e VAULT_ADDR=http://127.0.0.1:8200 \
  -e VAULT_TOKEN="$VAULT_ROOT_TOKEN" \
  vault vault write auth/approle/role/product-service \
  token_policies=primecart-common-read \
  token_ttl=1h \
  token_max_ttl=4h \
  secret_id_ttl=24h \
  bind_secret_id=true
```

Verify it:

```bash
docker compose \
  -f infra/vault/docker-compose-vault.yml \
  exec \
  -e VAULT_ADDR=http://127.0.0.1:8200 \
  -e VAULT_TOKEN="$VAULT_ROOT_TOKEN" \
  vault vault read auth/approle/role/product-service
```

## 11. Retrieve AppRole credentials

Retrieve the Role ID:

```bash
export VAULT_ROLE_ID="$(
  docker compose \
    -f infra/vault/docker-compose-vault.yml \
    exec -T \
    -e VAULT_ADDR=http://127.0.0.1:8200 \
    -e VAULT_TOKEN="$VAULT_ROOT_TOKEN" \
    vault vault read \
    -field=role_id \
    auth/approle/role/product-service/role-id
)"
```

Generate a Secret ID:

```bash
export VAULT_SECRET_ID="$(
  docker compose \
    -f infra/vault/docker-compose-vault.yml \
    exec -T \
    -e VAULT_ADDR=http://127.0.0.1:8200 \
    -e VAULT_TOKEN="$VAULT_ROOT_TOKEN" \
    vault vault write \
    -field=secret_id \
    -f \
    auth/approle/role/product-service/secret-id
)"
```

Confirm that both variables are populated without printing their contents:

```bash
echo "Role ID length: ${#VAULT_ROLE_ID}"
echo "Secret ID length: ${#VAULT_SECRET_ID}"
```

Both lengths must be greater than zero.

Environment variables are scoped to the current shell. Set them again when opening a new terminal. The Secret ID in this learning setup expires after 24 hours.

## 12. Test AppRole access

Obtain an application token:

```bash
export VAULT_APP_TOKEN="$(
  docker compose \
    -f infra/vault/docker-compose-vault.yml \
    exec -T \
    -e VAULT_ADDR=http://127.0.0.1:8200 \
    vault vault write \
    -field=token \
    auth/approle/login \
    role_id="$VAULT_ROLE_ID" \
    secret_id="$VAULT_SECRET_ID"
)"
```

Confirm the token exists without printing it:

```bash
echo "Application token length: ${#VAULT_APP_TOKEN}"
```

Check its policy:

```bash
docker compose \
  -f infra/vault/docker-compose-vault.yml \
  exec \
  -e VAULT_ADDR=http://127.0.0.1:8200 \
  -e VAULT_TOKEN="$VAULT_APP_TOKEN" \
  vault vault token lookup
```

Expected policies:

```text
default
primecart-common-read
```

Confirm read access:

```bash
docker compose \
  -f infra/vault/docker-compose-vault.yml \
  exec \
  -e VAULT_ADDR=http://127.0.0.1:8200 \
  -e VAULT_TOKEN="$VAULT_APP_TOKEN" \
  vault vault kv get kv/primecart/common
```

Confirm unrelated paths are denied:

```bash
docker compose \
  -f infra/vault/docker-compose-vault.yml \
  exec \
  -e VAULT_ADDR=http://127.0.0.1:8200 \
  -e VAULT_TOKEN="$VAULT_APP_TOKEN" \
  vault vault token capabilities kv/data/primecart/order-service
```

Expected:

```text
deny
```

## 13. Add Spring Cloud Vault to Product Service

Product Service uses Spring Boot `3.5.x`, so use the Spring Cloud `2025.0.x` release train.

Add this property to `product-service/pom.xml`:

```xml
<spring-cloud.version>2025.0.0</spring-cloud.version>
```

Add the dependency:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-vault-config</artifactId>
</dependency>
```

Import the Spring Cloud BOM inside `dependencyManagement`:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-dependencies</artifactId>
    <version>${spring-cloud.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

## 14. Configure Product Service

Add the following under `spring` in `product-service/src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: product-service

  config:
    import: vault://

  cloud:
    vault:
      uri: http://127.0.0.1:8200
      authentication: APPROLE
      fail-fast: true

      app-role:
        role-id: ${VAULT_ROLE_ID}
        secret-id: ${VAULT_SECRET_ID}
        app-role-path: approle

      kv:
        enabled: true
        backend: kv
        backend-version: 2
        default-context: primecart/common
        application-name: primecart/common
```

Use `vault://`, not `vault:///primecart/common`. The contextual form bypasses the configured KV mapping and can incorrectly request `/v1/primecart/common`. The standard import uses the configured `kv` backend and correctly requests `/v1/kv/data/primecart/common`.

Replace hard-coded Spring Boot Admin credentials:

```yaml
spring:
  boot:
    admin:
      client:
        url: http://localhost:9191
        username: ${SBA_USERNAME}
        password: ${SBA_PASSWORD}
```

Spring Cloud Vault exposes stored keys as Spring properties without adding a prefix, so `${SBA_USERNAME}` and `${SBA_PASSWORD}` resolve directly.

## 15. Start Product Service

Ensure Vault is running and unsealed. In the terminal that starts Product Service:

```bash
export VAULT_ROLE_ID='YOUR_PRODUCT_SERVICE_ROLE_ID'
export VAULT_SECRET_ID='YOUR_PRODUCT_SERVICE_SECRET_ID'
```

Compile and start:

```bash
cd product-service
mvn clean compile
mvn spring-boot:run
```

Expected flow:

1. Product Service authenticates through `auth/approle/login`.
2. Vault issues a restricted token.
3. Spring Cloud Vault reads `kv/data/primecart/common`.
4. Vault exposes `SBA_USERNAME` and `SBA_PASSWORD` as properties.
5. Product Service registers with Spring Boot Admin.

Do not provide `VAULT_APP_TOKEN` to Spring. Spring Cloud Vault obtains and manages its own token using the Role ID and Secret ID.

## Daily startup checklist

1. Start Vault:

   ```bash
   docker compose -f infra/vault/docker-compose-vault.yml up -d
   ```

2. Check whether Vault is sealed:

   ```bash
   docker compose -f infra/vault/docker-compose-vault.yml exec \
     -e VAULT_ADDR=http://127.0.0.1:8200 \
     vault vault status
   ```

3. Unseal Vault if required:

   ```bash
   docker compose -f infra/vault/docker-compose-vault.yml exec \
     -e VAULT_ADDR=http://127.0.0.1:8200 \
     vault vault operator unseal YOUR_UNSEAL_KEY
   ```

4. Export the Product Service AppRole credentials:

   ```bash
   export VAULT_ROLE_ID='...'
   export VAULT_SECRET_ID='...'
   ```

5. Start Product Service:

   ```bash
   cd product-service
   mvn spring-boot:run
   ```

## Restart and data-safety behavior

| Operation | Secrets preserved? | Manual unseal required? |
|---|---:|---:|
| `docker compose restart` | Yes | Yes |
| Docker Desktop restart | Yes | Yes |
| `docker compose down` then `up` | Yes | Yes |
| `docker compose down -v` | No | Not applicable |
| Delete the `vault-data` volume | No | Not applicable |

Never run `docker compose down -v` unless destroying all Vault data is intentional.

## Troubleshooting

### `disable_mlock must be configured`

Add this top-level HCL setting:

```hcl
disable_mlock = true
```

### `permission denied` for `/vault/data/vault.db`

The persistent volume is owned by the wrong user. Keep the `vault-init` service that assigns ownership to Vault UID/GID `100:1000`.

### `bind: address already in use` inside the Vault container

Use:

```yaml
command: server
```

Do not add `-config=/vault/config/vault.hcl`; the official image entrypoint already loads `/vault/config`.

### Application token length is zero

The environment variable was not set in the current shell. Generate or capture a new token, or export the Role ID and Secret ID again.

Check variables without printing their contents:

```bash
echo "Role ID length: ${#VAULT_ROLE_ID}"
echo "Secret ID length: ${#VAULT_SECRET_ID}"
echo "Application token length: ${#VAULT_APP_TOKEN}"
```

### `403` on `sys/internal/ui/mounts`

Confirm that the policy uses the actual mount name. This project uses `kv/`, so the KV version 2 policy path starts with:

```text
kv/data/...
```

not:

```text
secret/data/...
```

### `No value found at secret/data/primecart/common`

The wrong mount was used. The correct logical path is:

```text
kv/primecart/common
```

The corresponding API path is:

```text
kv/data/primecart/common
```

### Spring requests `/v1/primecart/common`

Replace:

```yaml
spring.config.import: vault:///primecart/common
```

with:

```yaml
spring.config.import: vault://
```

Then configure `spring.cloud.vault.kv.backend`, `default-context`, and `application-name` as shown above.

## Security notes

- Never commit Vault root tokens, unseal keys, Role IDs, Secret IDs, or passwords.
- Use the root token only for initial configuration and administration.
- Give each service its own AppRole and least-privilege policy.
- Do not expose Actuator `/env` merely to verify secrets.
- Do not use plaintext HTTP outside local development.
- Add TLS, audit logging, multiple unseal key shares, and auto-unseal before treating this as production infrastructure.
- Avoid using a shared human dashboard account as the long-term service-registration identity.
