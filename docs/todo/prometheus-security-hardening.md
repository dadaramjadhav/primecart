# Prometheus Security Hardening TODO

## 1. Purpose

This document records the security work required for the PrimeCart Prometheus
deployment. It separates controls already applied in the local environment
from remaining production-hardening tasks.

Prometheus security has two distinct boundaries:

```text
Users and tools
      |
      | Prometheus UI and HTTP API
      v
Prometheus :9090
      |
      | authenticated/private metric scraping
      v
Spring Boot /actuator/prometheus endpoints
```

Both boundaries must be protected. Securing the Prometheus UI alone does not
secure the service metrics endpoints.

## 2. Current State

The current observability Compose configuration:

- Binds Prometheus to host loopback at `127.0.0.1:9090`.
- Mounts `prometheus.yml` read-only.
- Stores Prometheus TSDB data in a named Docker volume.
- Places Prometheus on the shared external `primecart-nw` network.
- Uses the mutable `prom/prometheus:latest` image.
- Does not enable Prometheus UI/API authentication or TLS.
- Scrapes services through `host.docker.internal`.
- Scrapes most targets over plain HTTP.
- Exposes `/actuator/prometheus` with `permitAll()` in the services.

The loopback binding is an appropriate local-development improvement:

```yaml
ports:
  - "127.0.0.1:9090:9090"
```

It prevents direct access to port `9090` from other machines on the local
network. It is not sufficient as a production security boundary.

## 3. Immediate Local Hardening

### 3.1 Pin the Prometheus image

Replace:

```yaml
image: prom/prometheus:latest
```

with an approved, tested version:

```yaml
image: prom/prometheus:<approved-version>
```

For reproducible production deployment, pin its digest:

```yaml
image: prom/prometheus:<approved-version>@sha256:<verified-digest>
```

The pinned image still requires a regular update process. A permanently pinned
old digest will not receive security fixes.

Scan the selected image:

```bash
trivy image prom/prometheus:<approved-version>
```

### 3.2 Harden the container

Add:

```yaml
security_opt:
  - no-new-privileges:true

cap_drop:
  - ALL
```

Keep the configuration read-only:

```yaml
volumes:
  - ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml:ro
  - prometheus-data:/prometheus
```

The TSDB path must remain writable. Do not make the complete container
filesystem read-only until `/prometheus` and any required temporary paths have
been tested with explicit writable mounts.

### 3.3 Configure retention and resource limits

Add bounded retention:

```yaml
command:
  - "--config.file=/etc/prometheus/prometheus.yml"
  - "--storage.tsdb.path=/prometheus"
  - "--storage.tsdb.retention.time=15d"
  - "--storage.tsdb.retention.size=10GB"
```

Example development resource limits:

```yaml
mem_limit: 2g
cpus: 1.0
```

Tune these limits using actual scrape volume and query load.

### 3.4 Avoid unnecessary administrative APIs

Do not enable these flags unless they are required:

```text
--web.enable-admin-api
--web.enable-lifecycle
```

The admin API includes destructive TSDB operations. The lifecycle endpoint
permits HTTP-triggered configuration reloads. If either is enabled, UI/API
authentication and network restrictions become mandatory.

## 4. Restrict Network Exposure

### Local development

Retain:

```yaml
ports:
  - "127.0.0.1:9090:9090"
```

Grafana accesses Prometheus by Docker service name:

```text
http://prometheus:9090
```

### Production

Do not publish Prometheus directly:

```yaml
prometheus:
  expose:
    - "9090"
```

Remove the `ports` mapping. Administrators should access metrics through:

- Grafana
- A private VPN
- An authenticated ingress
- An SSH tunnel
- A Kubernetes port-forward

### Dedicated monitoring network

Create a monitoring network rather than placing all infrastructure on one
broad shared network:

```yaml
networks:
  monitoring-nw:
    internal: true
```

Attach only the components that need metric access:

```text
Prometheus
Grafana
Alertmanager, when used
Exporters
Monitored services
```

When services run in Docker, prefer internal service targets:

```yaml
static_configs:
  - targets:
      - inventory-service:9084
```

instead of:

```yaml
static_configs:
  - targets:
      - host.docker.internal:8084
```

An `internal` Docker network cannot be introduced while Prometheus still
depends on host services without testing Docker Desktop routing and external
connectivity.

## 5. Limit Spring Boot Actuator Exposure

Expose only the endpoints that monitoring requires:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,prometheus

  endpoint:
    health:
      show-details: when-authorized
```

Do not publicly expose sensitive endpoints such as:

```text
/actuator/env
/actuator/configprops
/actuator/beans
/actuator/heapdump
/actuator/threaddump
/actuator/loggers
/actuator/mappings
```

The current service security configurations explicitly permit:

```java
.requestMatchers(
        "/actuator/health/**",
        "/actuator/info",
        "/actuator/prometheus"
)
.permitAll()
```

This is acceptable only when the endpoint is protected by a private monitoring
network and cannot be reached through a public route.

## 6. Use a Separate Management Port

A production-oriented service should separate business traffic from monitoring
traffic:

```yaml
server:
  port: 8084

management:
  server:
    port: 9084

  endpoints:
    web:
      exposure:
        include: health,prometheus
```

The resulting boundary is:

```text
8084 -> application network
9084 -> monitoring network only
```

Prometheus then scrapes:

```yaml
- job_name: inventory-service
  metrics_path: /actuator/prometheus
  static_configs:
    - targets:
        - inventory-service:9084
```

Apply firewall, security-group, or Kubernetes NetworkPolicy rules so that only
Prometheus can connect to the management ports.

Changing management ports across PrimeCart requires coordinated updates to:

- Prometheus targets
- Spring Boot Admin registration
- Docker or Kubernetes health checks
- Operational documentation

## 7. Authenticate Scrape Requests

PrimeCart can use either private-network trust or OAuth2 client credentials.

### Option A: Private monitoring network

Keep `/actuator/prometheus` unauthenticated only when:

- It is not routed through the API Gateway.
- It has no public ingress.
- A firewall or network policy restricts access to Prometheus.
- The management network is isolated.
- TLS or mTLS protects sensitive internal environments.

This is the simpler deployment model.

### Option B: Keycloak client credentials

PrimeCart already uses Keycloak, so Prometheus can authenticate as a service.

Change each service security rule:

```java
.requestMatchers("/actuator/health/**")
.permitAll()

.requestMatchers("/actuator/prometheus")
.hasRole("ACTUATOR_ADMIN")
```

Create a confidential Keycloak client:

```text
Client ID: prometheus
Client authentication: On
Service accounts: Enabled
Role: ACTUATOR_ADMIN
```

The token must contain the audience expected by each destination service.
Do not disable audience validation merely to make scraping work.

Mount the client secret as a secret file:

```yaml
volumes:
  - ./secrets/prometheus-client-secret:/run/secrets/prometheus-client-secret:ro
```

Example scrape authentication:

```yaml
- job_name: inventory-service
  metrics_path: /actuator/prometheus

  oauth2:
    client_id: prometheus
    client_secret_file: /run/secrets/prometheus-client-secret
    token_url: https://host.docker.internal:8443/realms/primecart/protocol/openid-connect/token

    tls_config:
      ca_file: /etc/prometheus/certs/rootCA.pem
      server_name: localhost

  static_configs:
    - targets:
        - host.docker.internal:8084
```

The client secret and TLS private keys must not be committed to Git.

## 8. Protect the Prometheus UI and API

Prometheus supports TLS and bcrypt-hashed Basic Authentication through
`--web.config.file`.

Example `web-config.yml`:

```yaml
tls_server_config:
  cert_file: /etc/prometheus/certs/prometheus-cert.pem
  key_file: /etc/prometheus/certs/prometheus-key.pem
  min_version: TLS12

basic_auth_users:
  monitoring_admin: "<bcrypt-password-hash>"

http_server_config:
  headers:
    X-Content-Type-Options: nosniff
    X-Frame-Options: deny
```

Enable it:

```yaml
command:
  - "--config.file=/etc/prometheus/prometheus.yml"
  - "--storage.tsdb.path=/prometheus"
  - "--web.config.file=/etc/prometheus/web-config.yml"
```

Mount configuration and certificates read-only:

```yaml
volumes:
  - ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml:ro
  - ./prometheus/web-config.yml:/etc/prometheus/web-config.yml:ro
  - ./prometheus/certs:/etc/prometheus/certs:ro
  - prometheus-data:/prometheus
```

Prometheus expects bcrypt password hashes, not plaintext passwords. Treat the
hash as sensitive configuration and manage production credentials through an
approved secret-management process.

For user-facing production access, an OIDC-aware reverse proxy is preferable
to one shared Basic Auth account because it provides individual identities and
centralized access control.

## 9. Secure Grafana-to-Prometheus Access

When Prometheus has no published host port and both containers use a private
network, Grafana can use:

```yaml
url: http://prometheus:9090
```

If Prometheus enables TLS and Basic Auth, provision Grafana using environment
or secret-backed credentials:

```yaml
apiVersion: 1

datasources:
  - name: prometheus
    uid: prometheus
    type: prometheus
    access: proxy
    url: https://prometheus:9090
    basicAuth: true
    basicAuthUser: ${PROMETHEUS_USERNAME}

    jsonData:
      tlsAuthWithCACert: true
      serverName: prometheus

    secureJsonData:
      basicAuthPassword: ${PROMETHEUS_PASSWORD}
      tlsCACert: ${PROMETHEUS_CA_CERT}
```

Do not commit real passwords, tokens, private keys, or production CA private
material.

## 10. TLS for Service Scraping

When a target uses HTTPS:

```yaml
- job_name: api-gateway
  scheme: https
  metrics_path: /actuator/prometheus

  tls_config:
    ca_file: /etc/prometheus/certs/rootCA.pem
    server_name: localhost

  static_configs:
    - targets:
        - host.docker.internal:8181
```

The current API Gateway is configured for HTTPS, while its Prometheus target
does not specify `scheme: https`. Verify and correct this before treating the
target as operational.

Never use this as the permanent solution:

```yaml
tls_config:
  insecure_skip_verify: true
```

## 11. Scrape Safety Limits

Use limits to reduce the impact of accidental metric explosions:

```yaml
global:
  scrape_interval: 15s
  scrape_timeout: 10s

scrape_configs:
  - job_name: inventory-service
    sample_limit: 50000
    label_limit: 50
    label_name_length_limit: 100
    label_value_length_limit: 200
```

This is especially relevant to the inventory quantity gauge because it creates
one time series per product and SKU.

Set limits after measuring normal series volume. Limits that are too low cause
the complete scrape to fail.

## 12. Secret Management

Never commit:

```text
Prometheus Basic Auth plaintext password
Keycloak Prometheus client secret
Bearer tokens
TLS private keys
Gmail App Password
Grafana admin password
```

Store secrets in:

- Docker secrets
- Kubernetes Secrets backed by an external secret manager
- Vault
- CI/CD secret storage
- Environment files excluded by `.gitignore` for local development

Commit only secret references:

```yaml
client_secret_file: /run/secrets/prometheus-client-secret
```

## 13. Verification

### Confirm local network restriction

From the local machine:

```bash
curl -I http://127.0.0.1:9090
```

From another machine on the network, port `9090` should not be reachable.

### Confirm target status

Open locally:

```text
http://127.0.0.1:9090/targets
```

All expected jobs should be `UP`.

### Confirm protected scrape behavior

Without credentials:

```bash
curl -i http://localhost:8084/actuator/prometheus
```

Expected after authentication is enabled:

```text
401 Unauthorized
```

Prometheus should continue reporting:

```promql
up{job="inventory-service"} == 1
```

### Confirm TLS verification

```bash
curl --cacert "$(mkcert -CAROOT)/rootCA.pem" \
  -I https://localhost:9090
```

Expected:

```text
SSL certificate verify ok
```

## 14. Implementation Order

### Phase 1: Local hardening

- [x] Bind Prometheus to `127.0.0.1:9090`.
- [x] Mount `prometheus.yml` read-only.
- [ ] Pin the Prometheus image version and digest.
- [ ] Scan the Prometheus image with Trivy.
- [ ] Add `no-new-privileges` and drop Linux capabilities.
- [ ] Add retention and resource limits.
- [ ] Correct the API Gateway scrape to use HTTPS and CA validation.
- [ ] Limit exposed Actuator endpoints to what is required.

### Phase 2: Monitoring isolation

- [ ] Create a dedicated monitoring network.
- [ ] Run application services on Docker or an orchestrated private network.
- [ ] Replace `host.docker.internal` targets with service DNS names.
- [ ] Introduce dedicated management ports.
- [ ] Remove the Prometheus host port in production.
- [ ] Restrict management ports with firewall or network policies.

### Phase 3: Authentication and encryption

- [ ] Choose private-network-only or Keycloak client-credentials scraping.
- [ ] Remove `permitAll()` from `/actuator/prometheus` when using authentication.
- [ ] Configure the Keycloak Prometheus service account and audiences.
- [ ] Store the client secret outside Git.
- [ ] Enable TLS or mTLS for sensitive scrape traffic.
- [ ] Protect the Prometheus UI/API with an authenticated ingress or web config.
- [ ] Configure Grafana for the protected Prometheus data source.

### Phase 4: Operational maturity

- [ ] Provision Prometheus, Grafana dashboards, and alert rules from Git.
- [ ] Back up Prometheus data only when business requirements justify it.
- [ ] Monitor Prometheus and Grafana themselves.
- [ ] Alert on scrape failures, storage pressure, rule failures, and query errors.
- [ ] Test that monitoring alerts still work during application outages.
- [ ] Add an external black-box check so failure of the monitoring stack itself
  is detectable.

## 15. Production Target Architecture

```text
Users
  |
  | SSO / VPN / authenticated ingress
  v
Grafana
  |
  | private authenticated connection
  v
Prometheus
  |
  | TLS or mTLS / OAuth2 / network policy
  v
Dedicated service management ports
  |
  +--> /actuator/health
  +--> /actuator/prometheus
```

Prometheus should not be a public application endpoint. Grafana should be the
normal user interface, while direct Prometheus access is reserved for
authorized operators.

## 16. References

- [Prometheus HTTPS and authentication](https://prometheus.io/docs/prometheus/latest/configuration/https/)
- [Prometheus scrape configuration](https://prometheus.io/docs/prometheus/latest/configuration/configuration/)
- [Spring Boot Actuator endpoint security](https://docs.spring.io/spring-boot/3.5/reference/actuator/endpoints.html)
