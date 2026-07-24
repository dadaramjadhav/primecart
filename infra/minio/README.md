# PrimeCart Local Object Storage

MinIO provides an S3-compatible object store for product images during local
development. Its API and administration console are bound to localhost and
are not exposed to other machines on the network.

## Start MinIO

Create the shared Docker network if it does not already exist:

```bash
docker network inspect primecart-nw >/dev/null 2>&1 \
  || docker network create primecart-nw
```

Create the local environment file:

```bash
cd infra/minio
cp .env.example .env
```

Replace the example password in `.env`, and then start the service:

```bash
docker compose --env-file .env -f docker-compose-minio.yml up -d
```

Verify its health:

```bash
docker compose --env-file .env -f docker-compose-minio.yml ps
curl http://localhost:9000/minio/health/live
```

The endpoints are:

- S3-compatible API: `http://localhost:9000`
- MinIO console: `http://localhost:9001`

Use the root credentials only for local administration. Product Service will
receive a separate, least-privilege application account in a later step.

## Stop MinIO

```bash
docker compose --env-file .env -f docker-compose-minio.yml down
```

The `minio-data` volume preserves uploaded objects. Do not add `-v` unless the
local object data should also be deleted.
