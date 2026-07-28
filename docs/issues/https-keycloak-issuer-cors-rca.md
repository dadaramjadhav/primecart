# RCA: Orders Request Reported CORS Error After Enabling HTTPS

## Incident

- **Date:** 2026-07-27
- **Components:** PrimeCart UI, API Gateway, Config Server, Keycloak, NGINX
  Ingress
- **Status:** Resolved

## Symptom

The PrimeCart UI failed to load orders from:

```text
https://api.primecart.localhost/api/orders
```

The browser reported:

```text
Access to XMLHttpRequest has been blocked by CORS policy:
No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

The request originated from:

```text
https://primecart.localhost
```

## Initial Investigation

The API Gateway external configuration allowed the HTTPS UI origin:

```yaml
spring:
  cloud:
    gateway:
      server:
        webflux:
          globalcors:
            add-to-simple-url-handler-mapping: true
            cors-configurations:
              '[/**]':
                allowedOrigins:
                  - "https://primecart.localhost"
                allowedMethods:
                  - GET
                  - POST
                  - PUT
                  - DELETE
                  - OPTIONS
                allowedHeaders:
                  - "*"
                allowCredentials: true
```

A direct preflight request succeeded:

```text
HTTP/2 200
Access-Control-Allow-Origin: https://primecart.localhost
Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS
Access-Control-Allow-Credentials: true
```

This proved that the gateway CORS configuration was loaded and functioning.

The normal Orders request returned:

```text
HTTP/2 401
```

The custom unauthorized response did not include the CORS header, so the
browser surfaced the failed authentication as a CORS error.

## Root Cause

API Gateway had been updated to validate the HTTPS issuer:

```text
https://auth.primecart.localhost/realms/primecart
```

However, the live Keycloak Deployment still used:

```text
KC_HOSTNAME=http://auth.primecart.localhost
```

Keycloak therefore published:

```json
{
  "issuer": "http://auth.primecart.localhost/realms/primecart"
}
```

Tokens issued by Keycloak contained the HTTP issuer, while API Gateway expected
the HTTPS issuer. JWT issuer validation failed, API Gateway returned `401
Unauthorized`, and the browser presented the response as a CORS failure.

The repository manifest already contained the correct value:

```yaml
- name: KC_HOSTNAME
  value: https://auth.primecart.localhost
```

The manifest change had not been applied to the live Keycloak Deployment.

## Resolution

The corrected Keycloak manifest was applied:

```bash
kubectl apply \
  -f k8s/infrastructure/keycloak/keycloak.yml
```

The Keycloak rollout was verified:

```bash
kubectl rollout status deployment/keycloak \
  --namespace=primecart-infra \
  --timeout=10m
```

API Gateway was restarted so it loaded the latest external configuration:

```bash
kubectl rollout restart deployment/api-gateway \
  --namespace=primecart-app

kubectl rollout status deployment/api-gateway \
  --namespace=primecart-app \
  --timeout=10m
```

Users must clear existing browser tokens and log in again because old tokens
retain the HTTP issuer.

## Verification

Keycloak discovery returned the HTTPS URLs:

```text
issuer:
https://auth.primecart.localhost/realms/primecart

token endpoint:
https://auth.primecart.localhost/realms/primecart/protocol/openid-connect/token
```

The API Gateway preflight returned:

```text
HTTP/2 200
Access-Control-Allow-Origin: https://primecart.localhost
Access-Control-Allow-Credentials: true
```

These results confirmed that Keycloak and API Gateway agreed on the HTTPS
issuer and that CORS accepted the HTTPS UI origin.

## Preventive Actions

- Verify the live Deployment environment after changing Kubernetes manifests:

  ```bash
  kubectl get deployment keycloak \
    --namespace=primecart-infra \
    -o yaml
  ```

- Check Keycloak discovery after hostname or proxy changes:

  ```bash
  curl \
    https://auth.primecart.localhost/realms/primecart/.well-known/openid-configuration
  ```

- Confirm that the discovery `issuer` exactly matches the API Gateway
  `issuer-uri`.
- Test CORS preflight separately from authenticated API requests.
- Inspect the underlying HTTP status before assuming a browser message is a
  CORS configuration failure.
- Clear or invalidate old tokens after changing an OAuth issuer.
- Apply and verify Keycloak changes before restarting dependent applications.
