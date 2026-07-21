# Local Splunk Enterprise Setup

This guide records the common PrimeCart setup for running Splunk Enterprise
locally with Docker Compose. It covers the base Splunk installation and HTTP
Event Collector (HEC). Service-specific log pipelines are documented
separately.

The setup is intended for local learning and development, not production.

## Architecture

```text
Application or telemetry collector
              |
              | HTTP Event Collector (HEC), port 8088
              v
       Splunk Enterprise
              |
              +-- Index and search logs
              +-- Splunk Web, port 8000
```

PrimeCart currently uses HEC as the standard ingestion interface:

```text
Logstash or OpenTelemetry Collector
              |
              v
http://primecart-splunk:8088/services/collector
```

## Files

```text
infra/splunk/
├── .env
├── docker-compose-splunk.yml
└── README.md
```

Additional service-specific OTel Collector configuration can also live under
`infra/splunk`, but it is not required for the base Splunk server.

## Ports

| Port | Purpose |
|---|---|
| `8000` | Splunk Web UI |
| `8088` | HTTP Event Collector (HEC) |
| `8089` | Splunk management REST API |
| `9997` | Splunk Forwarder receiver for future use |

## 1. Create the shared Docker network

PrimeCart infrastructure containers communicate through:

```text
microservices-network
```

Check whether it exists:

```bash
docker network inspect microservices-network
```

Create it when it does not exist:

```bash
docker network create microservices-network
```

The network is external, so `docker compose down` does not delete it.

## 2. Create the local environment file

Create:

```text
infra/splunk/.env
```

Add:

```dotenv
SPLUNK_PASSWORD=<local-admin-password>
SPLUNK_HEC_TOKEN=<generated-local-hec-token>
```

Use a strong local password and a newly generated opaque HEC token. Do not use
the example placeholder values literally.

The repository ignores `.env` files. Confirm that the file is not tracked:

```bash
git status --short /Users/dadaramjadhav/primecart/infra/splunk/.env
```

Do not print the real values while troubleshooting. To confirm that variables
are populated without revealing them:

```bash
set -a
source /Users/dadaramjadhav/primecart/infra/splunk/.env
set +a

echo "SPLUNK_PASSWORD length: ${#SPLUNK_PASSWORD}"
echo "SPLUNK_HEC_TOKEN length: ${#SPLUNK_HEC_TOKEN}"
```

Both lengths must be greater than zero.

## 3. Configure the Splunk service

The base service in `infra/splunk/docker-compose-splunk.yml` is:

```yaml
services:
  splunk:
    image: splunk/splunk:10.2.5
    platform: linux/amd64

    container_name: primecart-splunk
    hostname: primecart-splunk

    environment:
      SPLUNK_START_ARGS: "--accept-license"
      SPLUNK_GENERAL_TERMS: "--accept-sgt-current-at-splunk-com"
      SPLUNK_PASSWORD: "${SPLUNK_PASSWORD}"

      SPLUNK_HEC_TOKEN: "${SPLUNK_HEC_TOKEN}"
      SPLUNK_HEC_ENABLE: "true"
      SPLUNK_HEC_SSL: "false"

      TZ: "Asia/Kolkata"

    ports:
      - "8000:8000"
      - "8088:8088"
      - "8089:8089"
      - "9997:9997"

    volumes:
      - splunk-etc:/opt/splunk/etc
      - splunk-var:/opt/splunk/var

    networks:
      - microservices-network

    restart: unless-stopped

networks:
  microservices-network:
    external: true

volumes:
  splunk-etc:
    name: primecart-splunk-etc

  splunk-var:
    name: primecart-splunk-var
```

### Configuration explanation

`SPLUNK_START_ARGS` accepts the Splunk license for this local container.

`SPLUNK_GENERAL_TERMS` accepts the current Splunk general terms required by the
selected image.

`SPLUNK_PASSWORD` initializes the local `admin` account. When persistent Splunk
configuration already exists, changing only the environment value might not
reset the password stored in Splunk.

`SPLUNK_HEC_ENABLE` enables HEC.

`SPLUNK_HEC_SSL: "false"` makes local HEC available over HTTP. Production HEC
must use HTTPS.

The named volumes persist configuration and indexed data across container
restarts and recreation.

The `linux/amd64` platform setting allows the selected Splunk image to run on
Apple Silicon through Docker emulation. Startup can be slower under emulation.

## 4. Validate Compose safely

Validate without displaying resolved environment values:

```bash
docker compose \
  --env-file /Users/dadaramjadhav/primecart/infra/splunk/.env \
  -f /Users/dadaramjadhav/primecart/infra/splunk/docker-compose-splunk.yml \
  config --quiet
```

No output means the configuration is valid.

Avoid running plain `docker compose config` in shared output because it expands
and prints environment values, including secrets.

## 5. Start Splunk

```bash
docker compose \
  --env-file /Users/dadaramjadhav/primecart/infra/splunk/.env \
  -f /Users/dadaramjadhav/primecart/infra/splunk/docker-compose-splunk.yml \
  up -d splunk
```

Check status:

```bash
docker compose \
  -f /Users/dadaramjadhav/primecart/infra/splunk/docker-compose-splunk.yml \
  ps
```

Splunk initialization can take several minutes. Wait for:

```text
primecart-splunk   Up (healthy)
```

Follow initialization logs:

```bash
docker compose \
  -f /Users/dadaramjadhav/primecart/infra/splunk/docker-compose-splunk.yml \
  logs -f splunk
```

Successful initialization ends with output similar to:

```text
Ansible playbook complete, will begin streaming splunkd_stderr.log
```

## 6. Open Splunk Web

Open:

```text
http://localhost:8000
```

Log in with:

```text
Username: admin
Password: value from SPLUNK_PASSWORD
```

Do not use the HEC token as the UI password. The administrator password and HEC
token have different purposes.

## 7. Verify HEC configuration

In Splunk Web, open:

```text
Settings
→ Data Inputs
→ HTTP Event Collector
```

Confirm:

```text
Global HEC: Enabled
Token: Enabled
```

The Docker environment normally creates a token input named similarly to:

```text
splunk_hec_token
```

The HEC endpoint from the host is:

```text
http://localhost:8088/services/collector/event
```

The HEC endpoint from another container on `microservices-network` is:

```text
http://primecart-splunk:8088/services/collector
```

## 8. Send a test HEC event

Load the local environment file:

```bash
set -a
source /Users/dadaramjadhav/primecart/infra/splunk/.env
set +a
```

Send a harmless test event:

```bash
curl \
  -H "Authorization: Splunk ${SPLUNK_HEC_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
        "event": {
          "message": "PrimeCart Splunk HEC test",
          "service": "manual-test",
          "environment": "local"
        },
        "source": "manual-test",
        "sourcetype": "primecart:json",
        "index": "main"
      }' \
  http://localhost:8088/services/collector/event
```

Expected response:

```json
{"text":"Success","code":0}
```

The HEC response confirms acceptance. Splunk can still require a short indexing
delay or browser refresh before the event appears in search.

## 9. Search the test event

Open:

```text
Search & Reporting
```

Set the time range to:

```text
Last 15 minutes
```

Search:

```spl
index=main source="manual-test"
```

Or search by message:

```spl
index=main "PrimeCart Splunk HEC test"
```

Parse JSON fields:

```spl
index=main source="manual-test"
| spath
| table _time event.service event.environment event.message
```

If a new event is not immediately visible:

1. Refresh the Splunk page.
2. Change the time range to `All time` temporarily.
3. Confirm the HEC response was `code: 0`.
4. Confirm the selected index is `main`.

## 10. Connect log shippers and collectors

Applications should not call Splunk HEC in business code. Use a logging or
telemetry pipeline:

```text
Application
  → Logstash
  → Splunk HEC
```

or:

```text
Application JSON logs
  → Splunk OpenTelemetry Collector
  → Splunk HEC
```

Use this URL from containers:

```text
http://primecart-splunk:8088/services/collector
```

Use environment expansion for the token:

```yaml
token: ${env:SPLUNK_HEC_TOKEN}
```

or in Compose:

```yaml
environment:
  SPLUNK_HEC_TOKEN: "${SPLUNK_HEC_TOKEN}"
```

Never hard-code the real token in a collector configuration.

Customer Service's OTel migration is documented in:

```text
docs/customer-service/setup-splunk.md
```

## 11. Configure timezone display

Splunk stores event time independently of how the UI displays it. Configure the
user preference:

```text
Username
→ Preferences
→ Time zone
→ Asia/Kolkata
→ Save
```

For example, these timestamps represent the same instant:

```text
2026-07-21 12:00:00 IST
2026-07-21T06:30:00Z
```

Collectors should preserve the original event timestamp. Do not rewrite all
timestamps into local time merely for display.

## 12. Restart and shutdown safety

The Splunk data is stored in named volumes:

```text
primecart-splunk-etc
primecart-splunk-var
```

Safe restart:

```bash
docker compose \
  -f /Users/dadaramjadhav/primecart/infra/splunk/docker-compose-splunk.yml \
  restart splunk
```

Safe stop and start:

```bash
docker compose \
  -f /Users/dadaramjadhav/primecart/infra/splunk/docker-compose-splunk.yml \
  stop splunk

docker compose \
  --env-file /Users/dadaramjadhav/primecart/infra/splunk/.env \
  -f /Users/dadaramjadhav/primecart/infra/splunk/docker-compose-splunk.yml \
  up -d splunk
```

Safe container removal while preserving volumes:

```bash
docker compose \
  -f /Users/dadaramjadhav/primecart/infra/splunk/docker-compose-splunk.yml \
  down
```

Start it again with the environment file:

```bash
docker compose \
  --env-file /Users/dadaramjadhav/primecart/infra/splunk/.env \
  -f /Users/dadaramjadhav/primecart/infra/splunk/docker-compose-splunk.yml \
  up -d
```

Do not run this unless intentionally deleting Splunk configuration and indexed
data:

```bash
docker compose \
  -f /Users/dadaramjadhav/primecart/infra/splunk/docker-compose-splunk.yml \
  down -v
```

`down -v` deletes the named volumes. This removes indexed logs, users, HEC
configuration, and other persisted Splunk state.

## 13. Backup considerations

For local learning, named volumes provide restart persistence but are not a
backup. Inspect the volumes:

```bash
docker volume inspect primecart-splunk-etc
docker volume inspect primecart-splunk-var
```

Before experimenting with destructive operations, create a deliberate Docker
volume backup or accept that the local Splunk instance may need to be
reinitialized.

## 14. HEC token rotation

Rotate the HEC token whenever it has been:

- Printed in terminal output.
- Pasted into chat or an issue.
- Committed to Git.
- Written into application or Collector logs.
- Shared with someone who no longer needs it.

After rotation:

1. Replace `SPLUNK_HEC_TOKEN` in `infra/splunk/.env`.
2. Recreate every collector or shipper that consumes it.
3. Send a new manual HEC test event.
4. Disable the old token in Splunk.

Recreate a Collector using the new environment:

```bash
docker compose \
  --env-file /Users/dadaramjadhav/primecart/infra/splunk/.env \
  -f /Users/dadaramjadhav/primecart/infra/splunk/docker-compose-splunk.yml \
  up -d --force-recreate
```

## 15. Troubleshooting

### Splunk takes a long time to become healthy

The initial startup runs an Ansible-based configuration process and can take
several minutes, especially when `linux/amd64` is emulated on Apple Silicon.
Follow container logs and wait for the health status.

### Port is already in use

Check:

```bash
lsof -i :8000
lsof -i :8088
lsof -i :8089
lsof -i :9997
```

Stop the conflicting process or change the host side of the Compose port
mapping.

### HEC returns 401 or invalid token

Confirm the environment variable is populated without printing it. Confirm the
token is enabled in:

```text
Settings → Data Inputs → HTTP Event Collector
```

Then recreate the sending collector so it receives the current token.

### HEC returns success but search is empty

1. Set the search time range to `All time` temporarily.
2. Search the correct index, normally `index=main`.
3. Refresh the browser after a short indexing delay.
4. Search by exact message rather than extracted fields.
5. Confirm the token's allowed/default index includes `main`.

### A container cannot resolve `primecart-splunk`

Confirm both containers belong to `microservices-network`:

```bash
docker network inspect microservices-network
```

The hostname `primecart-splunk` is available only to containers sharing that
network. Host applications use `localhost:8088` instead.

### UI password changed in `.env` but login still uses the old password

The persisted Splunk configuration in `primecart-splunk-etc` can retain the
existing administrator account. Changing the environment file does not
necessarily overwrite an already-initialized account. Reset the password using
Splunk's supported administrator recovery flow rather than deleting volumes.

### Docker reports an architecture warning

The Compose file explicitly selects:

```yaml
platform: linux/amd64
```

This is expected on an ARM-based Mac for an amd64 image. Emulation can consume
more CPU and memory.

## 16. Security and production notes

- Local HEC currently uses unencrypted HTTP. Production must use HTTPS.
- Never expose ports `8088`, `8089`, or `9997` publicly without network controls.
- Store HEC tokens and administrator passwords in a secret manager.
- Use separate HEC tokens for different environments and workloads.
- Restrict tokens to the required indexes.
- Rotate exposed tokens immediately.
- Configure Splunk retention and storage limits deliberately.
- Back up production Splunk according to Splunk's supported procedures.
- Monitor HEC availability, indexing queues, disk usage, and license usage.
- Pin image versions and test upgrades before deployment.

## 17. Verification checklist

- [ ] `microservices-network` exists.
- [ ] `infra/splunk/.env` exists and is not tracked.
- [ ] Splunk Compose validation succeeds with `config --quiet`.
- [ ] `primecart-splunk` becomes healthy.
- [ ] Splunk Web opens at `http://localhost:8000`.
- [ ] Administrator login succeeds.
- [ ] Global HEC is enabled.
- [ ] HEC token is enabled.
- [ ] Manual HEC request returns `{"text":"Success","code":0}`.
- [ ] Manual event appears in `index=main`.
- [ ] Named volumes exist for configuration and indexed data.
- [ ] No credential or token is committed or logged.

