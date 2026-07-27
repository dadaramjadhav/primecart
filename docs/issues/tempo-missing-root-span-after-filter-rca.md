# RCA: Tempo Reported Root Span Not Yet Received

## Incident

- **Date:** 2026-07-27
- **Components:** OpenTelemetry Collector, Tempo, Grafana, Spring Boot
- **Status:** Resolved

## Symptom

After actuator spans were filtered in the OpenTelemetry Collector, Grafana
Tempo displayed:

```text
<root span not yet received>
```

Although actuator root spans no longer appeared in new searches, incomplete
traces containing child spans were still stored.

## Impact

- Tempo displayed broken or incomplete traces.
- Spring Security child spans appeared without their HTTP server parent.
- Trace searches contained confusing entries with no root operation.

Application traffic was not affected.

## Root Cause

The collector used the filter processor to drop individual spans:

```yaml
filter/drop_actuator:
  traces:
    span:
      - 'IsMatch(attributes["uri"], "^/actuator(/.*)?$")'
      - 'IsMatch(attributes["http.url"], "^/actuator(/.*)?$")'
      - 'IsMatch(name, "^http [a-z]+ /actuator(/.*)?$")'
```

This correctly matched and removed the HTTP server root span, such as:

```text
http get /actuator/info
```

However, actuator requests also produced child observations:

```text
security filterchain before
authorize request
secured request
security filterchain after
```

Those child spans did not contain the actuator URI attributes and therefore did
not match the filter. The collector exported them to Tempo with a parent span
ID that no longer existed in the exported trace.

Tempo consequently reported that the root span had not yet been received.

## Resolution

The span-level filter was replaced with the tail-sampling processor.
Tail sampling groups spans by trace ID, evaluates the accumulated trace, and
drops the entire trace when any span matches the actuator URI.

```yaml
tail_sampling/drop_actuator:
  decision_wait: 5s
  num_traces: 10000
  expected_new_traces_per_sec: 100
  decision_cache:
    sampled_cache_size: 10000
    non_sampled_cache_size: 10000
  policies:
    - name: drop-actuator
      type: drop
      drop:
        drop_sub_policy:
          - name: actuator-uri
            type: string_attribute
            string_attribute:
              key: uri
              values:
                - '^/actuator(/.*)?$'
              enabled_regex_matching: true
          - name: actuator-http-url
            type: string_attribute
            string_attribute:
              key: http.url
              values:
                - '^/actuator(/.*)?$'
              enabled_regex_matching: true
    - name: keep-all
      type: always_sample
```

The trace pipeline now runs tail sampling before batching:

```yaml
pipelines:
  traces:
    receivers:
      - otlp
    processors:
      - memory_limiter
      - tail_sampling/drop_actuator
      - batch
    exporters:
      - otlp/tempo
      - debug
```

## Deployment

The corrected collector configuration was applied:

```bash
kubectl apply \
  -f k8s/observability/otel-collector/otel-collector.yaml

kubectl rollout restart deployment/otel-collector \
  -n primecart-observe

kubectl rollout status deployment/otel-collector \
  -n primecart-observe \
  --timeout=5m
```

Application services did not require a restart.

## Verification

The collector started successfully with OpenTelemetry Collector Contrib
version `0.156.0`.

After a new monitoring interval, Tempo was queried for newly generated actuator
spans:

```traceql
{ name =~ ".*actuator.*" }
```

The query returned:

```json
{"traces":[]}
```

This confirmed that complete actuator traces, including their child spans, were
dropped before export.

Historical incomplete traces may continue to display until Tempo retention
removes them.

## Preventive Actions

- Do not use span-level filters when the requirement is to remove an entire
  trace.
- Use tail sampling for decisions based on any span within a trace.
- Verify trace completeness after filtering, not only the absence of the
  matched root span.
- Monitor collector tail-sampling memory and dropped-trace metrics.
- Keep all spans for a trace routed through the same collector instance.
- Pin the collector image version instead of using `latest`.

## Reference

The OpenTelemetry tail-sampling processor groups spans by trace ID before
making a sampling decision:

```text
https://github.com/open-telemetry/opentelemetry-collector-contrib/tree/main/processor/tailsamplingprocessor
```
