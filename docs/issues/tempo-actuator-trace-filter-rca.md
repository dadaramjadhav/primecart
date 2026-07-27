# RCA: Actuator Traces Still Appeared in Tempo

## Incident

- **Date:** 2026-07-27
- **Components:** Spring Boot applications, OpenTelemetry Collector, Tempo,
  Grafana
- **Status:** Resolved

## Summary

Spring Boot actuator requests continued to appear in Grafana Tempo after an
OpenTelemetry Collector filter was introduced to drop them.

Examples included:

```text
http get /actuator/info
http get /actuator/health
http get /actuator/health/**
http get /actuator/prometheus
```

## Impact

The traces were harmless but added frequent monitoring noise to Tempo searches,
making application traces harder to inspect.

## Initial Assumption

Application manifests contained:

```yaml
- name: OTEL_INSTRUMENTATION_HTTP_SERVER_EXCLUDE_PATTERNS
  value: "/actuator/info,/actuator/health.*,/actuator/prometheus"
```

This setting applies to OpenTelemetry Java agent HTTP instrumentation.
PrimeCart services use Micrometer Tracing with the OpenTelemetry bridge:

```text
micrometer-tracing-bridge-otel
opentelemetry-exporter-otlp
```

Because the services were not using the OpenTelemetry Java agent, the
environment variable did not exclude their actuator spans.

## First Collector Filter

A centralized collector filter was added:

```yaml
filter/drop_actuator:
  error_mode: ignore
  traces:
    span:
      - 'IsMatch(attributes["http.route"], "^/actuator(/.*)?$")'
      - 'IsMatch(attributes["url.path"], "^/actuator(/.*)?$")'
      - 'IsMatch(attributes["http.target"], "^/actuator(/.*)?$")'
```

The filter loaded without errors, but new actuator traces continued to reach
Tempo.

## Root Cause

The filter checked standard OpenTelemetry HTTP semantic-convention attribute
names, but the Micrometer-generated spans in this environment used different
attributes.

Inspection of a recent trace showed:

```text
span name: http get /actuator/info
uri: /actuator/info
http.url: /actuator/info
method: GET
status: 200
```

The span did not contain a matching `http.route`, `url.path`, or `http.target`
value. Consequently, none of the initial filter conditions evaluated to true.

## Resolution

The collector filter was expanded to cover the attributes actually emitted by
Micrometer and to include a span-name fallback:

```yaml
filter/drop_actuator:
  error_mode: ignore
  traces:
    span:
      - 'IsMatch(attributes["http.route"], "^/actuator(/.*)?$")'
      - 'IsMatch(attributes["url.path"], "^/actuator(/.*)?$")'
      - 'IsMatch(attributes["http.target"], "^/actuator(/.*)?$")'
      - 'IsMatch(attributes["uri"], "^/actuator(/.*)?$")'
      - 'IsMatch(attributes["http.url"], "^/actuator(/.*)?$")'
      - 'IsMatch(name, "^http [a-z]+ /actuator(/.*)?$")'
```

The filter remains before the batch processor in the trace pipeline:

```yaml
pipelines:
  traces:
    receivers:
      - otlp
    processors:
      - memory_limiter
      - filter/drop_actuator
      - batch
    exporters:
      - otlp/tempo
      - debug
```

The configuration was applied and the collector was restarted:

```bash
kubectl apply \
  -f k8s/observability/otel-collector/otel-collector.yaml

kubectl rollout restart deployment/otel-collector \
  -n primecart-observe

kubectl rollout status deployment/otel-collector \
  -n primecart-observe \
  --timeout=5m
```

Application services did not require a restart because filtering occurs
centrally in the collector.

## Verification

Tempo was queried for actuator spans created after the collector rollout:

```traceql
{ name =~ ".*actuator.*" }
```

The query returned:

```json
{"traces":[]}
```

This confirmed that newly generated actuator spans were no longer reaching
Tempo.

Previously stored actuator traces remain visible until Tempo retention removes
them.

## Preventive Actions

- Identify the tracing implementation before using instrumentation-specific
  environment variables.
- Inspect an actual exported span before writing collector attribute filters.
- Validate filters against newly generated traces, not only historical Tempo
  results.
- Retain a span-name fallback when instrumentation libraries use nonstandard
  or version-specific attribute names.
- Pin the OpenTelemetry Collector image version instead of relying on
  `latest`, because processor behavior and configuration support can change.
