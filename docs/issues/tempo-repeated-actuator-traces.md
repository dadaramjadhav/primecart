# Tempo Shows Repeated Actuator Traces

## Issue

Tempo repeatedly displays traces for Spring Boot actuator endpoints such as:

```text
GET /actuator/info
```

## Cause

Monitoring or health-check components periodically call actuator endpoints.
The OpenTelemetry Java agent instruments these HTTP requests by default, so
each poll creates a trace and sends it to Tempo.

This behavior is expected and harmless, but it adds noise to trace searches.

## Resolution

Exclude actuator endpoints from OpenTelemetry HTTP server instrumentation.
Add the following environment variable to each relevant service deployment:

```yaml
- name: OTEL_INSTRUMENTATION_HTTP_SERVER_EXCLUDE_PATTERNS
  value: "/actuator/info,/actuator/health.*,/actuator/prometheus"
```

If outgoing actuator requests should also be excluded, add:

```yaml
- name: OTEL_INSTRUMENTATION_HTTP_CLIENT_EXCLUDE_PATTERNS
  value: ".*/actuator/(info|health.*|prometheus)"
```

Apply the updated deployment manifest:

```bash
kubectl apply -f <service-deployment.yaml>
```

Restart the affected service and wait for the rollout:

```bash
kubectl rollout restart deployment/<service> -n primecart-app
kubectl rollout status deployment/<service> -n primecart-app
```

## Verification

1. Confirm the service pod is ready.
2. Wait for several monitoring intervals.
3. Search Tempo for new traces matching `/actuator/info`,
   `/actuator/health`, or `/actuator/prometheus`.
4. Confirm that application endpoint traces continue to appear.

Existing actuator traces remain visible until Tempo's retention period
expires.

## Notes

- Apply the exclusion only to services using the OpenTelemetry Java agent.
- Excluding actuator endpoints affects tracing only; it does not disable the
  endpoints or their health and metrics polling.
- Keep application endpoints instrumented so operationally useful traces are
  still exported.
