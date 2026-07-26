# Keycloak Realm Backup and Restore

This guide exports the `primecart` realm configuration and users from the
Keycloak instance defined in
`devops/keycloack/docker-compose-keycloak.yml`.

Run all commands from the repository root:

```bash
cd /Users/dadaramjadhav/primecart
```

## What the realm export contains

The export includes realm settings, clients, roles, groups, identity-provider
configuration, and users. Treat the exported JSON as sensitive because it may
contain client credentials and other security configuration.

It is not a complete Keycloak backup. Sessions, events, workflow state, and
revoked tokens are not included. Back up the Keycloak MySQL database separately
when full disaster recovery is required.

## Create a realm backup

### 1. Create the host backup directory

```bash
mkdir -p backups/keycloak
```

The temporary export container writes its output into this directory through a
bind mount, so the backup remains available after the container exits.

### 2. Stop Keycloak

Keycloak should be stopped while exporting to prevent inconsistent realm or
user data:

```bash
docker compose \
  -f devops/keycloack/docker-compose-keycloak.yml \
  stop keycloak
```

### 3. Export the `primecart` realm and its users

```bash
docker compose \
  -f devops/keycloack/docker-compose-keycloak.yml \
  run --rm --no-deps \
  -v "$(pwd)/backups/keycloak:/opt/keycloak/data/export" \
  keycloak export \
  --dir /opt/keycloak/data/export \
  --realm primecart \
  --users realm_file
```

The expected output file is:

```text
backups/keycloak/primecart-realm.json
```

### 4. Start Keycloak

```bash
docker compose \
  -f devops/keycloack/docker-compose-keycloak.yml \
  start keycloak
```

### 5. Verify the backup

```bash
ls -lh backups/keycloak/primecart-realm.json
```

Copy the file to an encrypted, access-controlled backup location. Do not commit
it to source control.

## Restore a realm backup

Restoring with `--override true` replaces an existing realm with the contents of
the backup. Confirm that the correct backup file is in `backups/keycloak` before
continuing.

### 1. Stop Keycloak

```bash
docker compose \
  -f devops/keycloack/docker-compose-keycloak.yml \
  stop keycloak
```

### 2. Import the backup

```bash
docker compose \
  -f devops/keycloack/docker-compose-keycloak.yml \
  run --rm --no-deps \
  -v "$(pwd)/backups/keycloak:/opt/keycloak/data/import:ro" \
  keycloak import \
  --dir /opt/keycloak/data/import \
  --override true
```

### 3. Start Keycloak

```bash
docker compose \
  -f devops/keycloack/docker-compose-keycloak.yml \
  start keycloak
```

### 4. Verify the restored realm

For an HTTP-only local Keycloak instance:

```bash
curl -fsS \
  http://localhost:8080/realms/primecart/.well-known/openid-configuration
```

Then sign in to the Keycloak Admin Console and verify the realm's clients,
roles, groups, and users.

## Export all realms

Omit `--realm primecart` to export every realm:

```bash
docker compose \
  -f devops/keycloack/docker-compose-keycloak.yml \
  run --rm --no-deps \
  -v "$(pwd)/backups/keycloak:/opt/keycloak/data/export" \
  keycloak export \
  --dir /opt/keycloak/data/export \
  --users realm_file
```

Stop Keycloak before this export and start it again afterward, as described
above.

## Reference

- [Keycloak realm import and export documentation](https://www.keycloak.org/server/importExport)
