# PrimeCart Security Controls

## 1. Purpose

This document describes the security controls currently implemented in PrimeCart, how they work together, how to verify them, and which development settings must be strengthened before production deployment.

Security is applied in layers:

```text
Browser
  │ HTTPS + OIDC/PKCE
  ▼
React UI ── HTTPS + bearer token ──> API Gateway
                                         │
                                         ├── JWT validation
                                         ├── route authorization
                                         └── internal service routing
                                                  │
                                                  ▼
                                        Spring Boot services
                                         ├── JWT validation
                                         ├── method authorization
                                         ├── ownership checks
                                         └── database/message controls
```

## 2. Transport Layer Security

### Local development certificates

Local HTTPS uses a development certificate signed by a private `mkcert` certificate authority. The certificate covers:

- `localhost`
- `127.0.0.1`
- `::1`

The files are generated locally under `infra/tls`:

```text
localhost-cert.pem    Server certificate
localhost-key.pem     Server private key
```

TLS private keys, certificates, trust stores, and environment files are excluded by `.gitignore`. Never commit or share `rootCA-key.pem` or a server private key.

### HTTPS endpoints

The local browser-facing endpoints are:

| Component | URL |
| --- | --- |
| React development UI | `https://localhost:5173` |
| API Gateway | `https://localhost:8181` |
| Keycloak | `https://localhost:8443` |

Vite reads the certificate and key in `primecart-ui/vite.config.js`. The API Gateway enables SSL in `api-gateway/src/main/resources/application.yml`. Keycloak mounts the same development certificate and key as read-only files.

HTTPS encrypts HTTP headers, bearer tokens, cookies, query parameters, request bodies, and response bodies between the client and the TLS endpoint. It does not encrypt data after TLS termination, at rest in a database, in application memory, or when application code writes it to logs.

### Verification

Verify each endpoint with the local CA:

```bash
curl --cacert "$(mkcert -CAROOT)/rootCA.pem" -v https://localhost:5173
curl --cacert "$(mkcert -CAROOT)/rootCA.pem" -v https://localhost:8181/actuator/health
curl --cacert "$(mkcert -CAROOT)/rootCA.pem" -v https://localhost:8443
```

Expected output includes:

```text
SSL certificate verify ok
SSL connection using TLSv1.3
```

The Keycloak endpoint currently negotiates TLS 1.3 with `TLS_AES_256_GCM_SHA384` and HTTP/2 when supported by the client.

## 3. Identity and Authentication

PrimeCart delegates user authentication to Keycloak. PrimeCart services do not need to store user passwords.

The React UI uses the Keycloak JavaScript adapter. It obtains an access token and attaches it to API requests:

```http
Authorization: Bearer <access-token>
```

The Axios client refreshes an expiring token and retries a request once after a `401` response. If refresh fails, the local session is cleared and the user is redirected to login.

Keycloak remains the authority for:

- User credentials
- Login sessions
- Access and refresh tokens
- Realm and client roles
- Password policies and resets
- Optional multi-factor authentication

Application databases should store only the Keycloak subject identifier (`sub`) and application-specific profile data.

## 4. JWT Validation

The API Gateway and resource services validate bearer tokens independently. Validation includes:

- Cryptographic signature validation
- Issuer validation using `issuer-uri`
- Standard lifetime validation
- Application-specific audience validation

The issuer and expected audience validators are combined with `DelegatingOAuth2TokenValidator`. A correctly signed token for the wrong service audience is rejected.

Keycloak realm roles are converted into Spring Security authorities with the `ROLE_` prefix. For example:

```text
PRODUCT_READ → ROLE_PRODUCT_READ
```

## 5. Authorization

### Gateway authorization

The API Gateway enforces HTTP method and path authorization for product, category, brand, order, cart, inventory, payment, and customer-profile routes.

Examples include:

```text
GET    /api/products/**       PRODUCT_READ
POST   /api/products/**       PRODUCT_CREATE
DELETE /api/cart              CART_CLEAR
GET    /api/payments/**       PAYMENT_READ
PUT    /api/customers/me      PROFILE_UPDATE
```

Internal service-to-service routes are denied at the gateway, including inventory reservation operations and payment-driven order transitions. These routes are intended to be called directly by authorized services using service credentials.

### Service authorization

Services validate JWTs again instead of trusting the gateway alone. Many business operations also use `@PreAuthorize`, providing protection if a service is reached directly.

Ownership checks restrict customer resources such as carts, payments, profiles, and orders to the authenticated subject. Administrative bypasses must be explicit and role-controlled.

### Management endpoints

Health, info, and Prometheus endpoints are currently public in service security configurations. More detailed Actuator endpoints require `ACTUATOR_ADMIN`.

For production, place metrics on a private management network or require authenticated Prometheus access. Avoid exposing health details that reveal dependencies or infrastructure.

## 6. Service-to-Service Authentication

PrimeCart uses two token-propagation patterns:

- Calls made on behalf of a user forward the current user bearer token.
- Internal calls use the OAuth2 client-credentials flow to obtain a service token.

Service tokens are sent in the `Authorization` header and are validated by the destination service using issuer, audience, and role checks.

Never log incoming or outgoing access tokens. Logging subjects, audiences, expiration times, or correlation IDs may be acceptable when needed, but logs must not contain raw credentials.

## 7. Browser Security Headers

The Vite development server defines a Content Security Policy with restrictions for scripts, images, fonts, network connections, frames, objects, forms, and frame ancestors.

Current CSP delivery uses:

```text
Content-Security-Policy-Report-Only
```

Report-only mode detects violations but does not block them. After resolving legitimate reports, production should enforce the policy with `Content-Security-Policy`.

The Nginx configuration also sets:

- `X-Content-Type-Options: nosniff`
- `Referrer-Policy: strict-origin-when-cross-origin`
- `X-Frame-Options: DENY`

Before production deployment, update Nginx to listen on HTTPS and replace its remaining HTTP development origins with production HTTPS origins.

## 8. CSRF and CORS

The APIs use bearer-token authentication and disable CSRF protection. This is appropriate only while authentication is carried in the `Authorization` header rather than automatically submitted authentication cookies.

If authentication moves to cookies, enable CSRF protection and use secure cookie attributes:

```text
Secure
HttpOnly
SameSite
```

The API Gateway enables CORS support. Production CORS configuration must use an explicit allowlist of trusted UI origins, methods, and headers. Do not combine wildcard origins with credentials.

## 9. Secret Management

Configuration files reference environment variables instead of embedding secret values. Examples include:

```text
GITHUB_ACCESS_TOKEN
SBA_PASSWORD
KEYCLOAK_DB_PASSWORD
KEYCLOAK_ADMIN_PASSWORD
RABBITMQ_DEFAULT_PASS
SPLUNK_PASSWORD
SPLUNK_HEC_TOKEN
```

Secret-bearing `.env` files and TLS material are ignored by Git. Secrets should be supplied through the runtime environment, container secret mechanism, CI/CD secret store, or Vault.

The repository contains a Vault deployment and setup documentation, but its current listener disables TLS and uses HTTP on port `8200`. Treat this as local development configuration only. Enable TLS, restrict network access, use least-privilege policies, and protect recovery/unseal material before production.

Any credential that has entered Git history, a log, a screenshot, or a shared message must be revoked and rotated. Removing it only from the current file is insufficient.

## 10. Data and Database Protection

MySQL credentials are externalized. Application data access uses JPA repositories and parameterized queries, reducing SQL-injection exposure when repositories are used correctly.

Inventory and saga operations use database locking where concurrent state transitions require serialization. Event identifiers are stored with unique constraints to support idempotent message processing.

Production database controls should include:

- Separate database users per service
- Least-privilege grants
- TLS for database connections
- Encrypted backups
- Restricted network access
- Audited schema migrations
- Tested restore procedures

If PrimeCart ever stores passwords outside Keycloak, store only an adaptive salted hash such as Argon2id, bcrypt, or PBKDF2 in a `VARCHAR(255)` column. Never store plaintext, encrypted passwords, MD5, or fast general-purpose hashes.

## 11. Messaging Security and Reliability

RabbitMQ credentials are environment-backed. Saga consumers use processed-event records and unique event IDs to ignore duplicate messages. Some publishers run after a successful database commit, reducing the chance of publishing an event for a rolled-back transaction.

For production:

- Enable TLS for AMQP and the management interface.
- Create service-specific users and virtual hosts.
- Apply least-privilege exchange and queue permissions.
- Use dead-letter queues and bounded retries.
- Do not put access tokens, passwords, or unnecessary personal data in events.
- Consider a transactional outbox where reliable database-to-message publication is required.

## 12. Logging, Metrics, and Tracing

PrimeCart uses structured Logstash output, Prometheus metrics, and OpenTelemetry tracing. Trace and span identifiers allow requests to be correlated across services without logging credentials.

Do not log:

- Passwords
- Access or refresh tokens
- Authorization headers
- Session identifiers
- TLS private keys
- Vault tokens or unseal material
- Full payment or personal data

Restrict access to Splunk, Grafana, Prometheus, Tempo, Actuator, and Spring Boot Admin. Observability systems often contain sensitive operational data even when application secrets are correctly redacted.

## 13. Current Development Limitations

The following settings are not production-ready:

- `mkcert` certificates are trusted only on explicitly configured development machines.
- The React production Nginx template currently listens on HTTP.
- CSP is report-only rather than enforced.
- Internal service calls commonly use HTTP.
- Vault currently disables TLS.
- RabbitMQ management and AMQP endpoints currently use non-TLS ports.
- Splunk HEC currently disables SSL.
- Several health, info, and Prometheus endpoints are public.
- The API Gateway development token endpoint is permitted publicly.
- The React application currently contains a temporary access-token console log.

The last two items should be removed or restricted before sharing the environment:

```text
API Gateway: /dev/token
React UI: console.log of keycloak.token
```

## 14. Production Checklist

- Use certificates issued by a public CA or organization-managed private CA.
- Enforce HTTPS redirects and HSTS at the public ingress.
- Enable TLS or mTLS on sensitive internal hops.
- Remove development token endpoints and all raw token logging.
- Enforce CSP after validating reports.
- Restrict CORS to trusted production origins.
- Make management endpoints private or authenticated.
- Enable TLS and authentication for databases, RabbitMQ, Vault, and observability tools.
- Rotate development credentials before production.
- Use per-service identities and least-privilege permissions.
- Scan source code, Git history, images, and CI logs for secrets.
- Run dependency, container, and infrastructure vulnerability scans.
- Test authorization, ownership isolation, token audiences, replay/idempotency, and failure recovery.
- Document incident-response and credential-rotation procedures.
