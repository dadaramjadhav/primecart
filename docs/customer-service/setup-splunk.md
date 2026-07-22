# Customer Service Logs to Splunk with OpenTelemetry

This guide records the local PrimeCart setup used to send Customer Service logs
to Splunk Enterprise through the Splunk Distribution of the OpenTelemetry
Collector.

The setup is intended for local learning. It does not send logs directly from
Java to Splunk and does not place the Splunk HEC token in Customer Service.

## Architecture

```text
Customer Service
  |
  | Logback JSON rolling file
  v
/Users/dadaramjadhav/primecart/logs/customer-service.json
  |
  | Read by file_log receiver
  v
Splunk OpenTelemetry Collector
  |
  | Splunk HEC exporter
  v
Splunk Enterprise
  |
  v
index=main source="customer-service"
```

During migration, Customer Service can send logs to both destinations:

```text
Customer Service
  +-- LOGSTASH appender --> Logstash --> Elasticsearch/Kibana
  +-- JSON_FILE appender --> Splunk OTel Collector --> Splunk
```

After the Splunk path is verified, the Logstash appender can be removed from
Customer Service without affecting the OTel Collector path.

## Components and ports

| Component | Address | Purpose |
|---|---|---|
| Customer Service | `http://localhost:8086` | Produces application logs |
| Splunk UI | `http://localhost:8000` | Search and visualization |
| Splunk HEC | `http://localhost:8088` | Receives Collector log events |
| Customer OTel health | `http://localhost:13134` | Collector health endpoint |

The Collector and Splunk containers use the external Docker network:

```text
microservices-network
```

## 1. Prerequisites

The following are required:

- Docker and Docker Compose.
- The external `microservices-network` Docker network.
- Customer Service running locally on port `8086`.
- `logstash-logback-encoder` in the Customer Service POM.
- A local Splunk HEC token stored outside committed configuration.

Create the network if it does not exist:

```bash
docker network inspect microservices-network >/dev/null 2>&1 || \
docker network create microservices-network
```

Customer Service already has the JSON encoder dependency:

```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>8.1</version>
</dependency>
```

## 2. Configure Splunk and HEC

The Splunk service uses:

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
```

Store local credentials in:

```text
infra/splunk/.env
```

Example variable names:

```dotenv
SPLUNK_PASSWORD=<local-admin-password>
SPLUNK_HEC_TOKEN=<local-hec-token>
```

Do not commit `.env`. Do not copy the actual HEC token into documentation,
Logback XML, Collector YAML, source code, or logs.

Start Splunk:

```bash
docker compose \
  --env-file /Users/dadaramjadhav/primecart/infra/splunk/.env \
  -f /Users/dadaramjadhav/primecart/infra/splunk/docker-compose-splunk.yml \
  up -d splunk
```

Open Splunk:

```text
http://localhost:8000
```

## 3. Create the local log directories

```bash
mkdir -p /Users/dadaramjadhav/primecart/logs/archive
```

The repository `.gitignore` contains:

```gitignore
logs/
```

This prevents application logs from being committed.

## 4. Configure Customer Service JSON logging

The configuration file is:

```text
customer-service/src/main/resources/logback-spring.xml
```

Read the Spring application name:

```xml
<springProperty
        scope="context"
        name="APP_NAME"
        source="spring.application.name"/>
```

Add a rolling JSON file appender:

```xml
<appender name="JSON_FILE"
          class="ch.qos.logback.core.rolling.RollingFileAppender">

    <file>/Users/dadaramjadhav/primecart/logs/customer-service.json</file>

    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
        <fileNamePattern>
            /Users/dadaramjadhav/primecart/logs/archive/customer-service-%d{yyyy-MM-dd}.%i.json.gz
        </fileNamePattern>

        <maxFileSize>50MB</maxFileSize>
        <maxHistory>7</maxHistory>
        <totalSizeCap>500MB</totalSizeCap>
    </rollingPolicy>

    <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
        <providers>
            <timestamp/>
            <logLevel/>
            <loggerName/>
            <threadName/>
            <message/>
            <arguments/>
            <stackTrace/>
            <mdc/>

            <pattern>
                <pattern>
                    {
                      "service": "${APP_NAME}",
                      "environment": "local"
                    }
                </pattern>
            </pattern>
        </providers>
    </encoder>
</appender>
```

During migration, retain console, Logstash, and JSON file outputs:

```xml
<root level="INFO">
    <appender-ref ref="CONSOLE"/>
    <appender-ref ref="LOGSTASH"/>
    <appender-ref ref="JSON_FILE"/>
</root>
```

Restart Customer Service after changing Logback configuration.

Verify that it produces JSON:

```bash
tail -f /Users/dadaramjadhav/primecart/logs/customer-service.json
```

Generate a log:

```bash
curl http://localhost:8086/actuator/health
```

Example JSON event:

```json
{
  "@timestamp": "2026-07-21T21:49:56.212927+05:30",
  "level": "INFO",
  "logger_name": "com.primecart.service.CustomerProfileServiceImpl",
  "thread_name": "http-nio-8086-exec-3",
  "message": "Updating customer profile. profileId=5",
  "traceId": "example-trace-id",
  "spanId": "example-span-id",
  "service": "customer-service",
  "environment": "local"
}
```

Never put an access token, Authorization header, password, email address, phone
number, or request body into application logs.

## 5. Configure the Splunk OTel Collector

The Collector configuration is:

```text
infra/splunk/primecart-otel-collector.yaml
```

Use this configuration:

```yaml
receivers:
  file_log/customer:
    include:
      - /var/log/primecart/customer-service.json

    start_at: end
    include_file_path: true
    include_file_name: true
    storage: file_storage

    operators:
      - type: json_parser
        parse_from: body

processors:
  memory_limiter:
    check_interval: 5s
    limit_mib: 256
    spike_limit_mib: 64

  resource/customer:
    attributes:
      - key: service.name
        value: customer-service
        action: upsert

      - key: deployment.environment
        value: local
        action: upsert

  batch:
    timeout: 5s
    send_batch_size: 256

exporters:
  splunk_hec/customer:
    token: ${env:SPLUNK_HEC_TOKEN}
    endpoint: http://primecart-splunk:8088/services/collector
    source: customer-service
    sourcetype: primecart:json
    index: main

    tls:
      insecure: true

    retry_on_failure:
      enabled: true
      initial_interval: 5s
      max_interval: 30s
      max_elapsed_time: 5m

    sending_queue:
      enabled: true
      num_consumers: 2
      queue_size: 1000
      storage: file_storage

extensions:
  health_check:
    endpoint: 0.0.0.0:13133

  file_storage:
    directory: /var/lib/otelcol

service:
  extensions:
    - health_check
    - file_storage

  telemetry:
    logs:
      level: info

  pipelines:
    logs/customer:
      receivers:
        - file_log/customer

      processors:
        - memory_limiter
        - resource/customer
        - batch

      exporters:
        - splunk_hec/customer
```

Important: the token line must use environment expansion:

```yaml
token: ${env:SPLUNK_HEC_TOKEN}
```

Do not use the actual token value in this file. If a token has been committed,
printed, or shared, rotate it in Splunk and update `infra/splunk/.env`.

`start_at: end` means the Collector sends new entries after its initial start.
It does not resend the whole existing file. The file-storage extension records
the read position across Collector restarts.

## 6. Configure persistent volume permissions

The Collector runs as a non-root user. A new Docker named volume is normally
owned by root, so the Collector can initially fail with:

```text
failed to start "splunk_hec/customer" exporter
open /var/lib/otelcol/exporter_splunk_hec_customer_logs: permission denied
```

Add a volume initialization service:

```yaml
  primecart-otel-state-init:
    image: busybox:1.36
    container_name: primecart-otel-state-init

    command:
      - sh
      - -c
      - |
        mkdir -p /var/lib/otelcol
        chmod -R 0777 /var/lib/otelcol

    volumes:
      - primecart-otel-state:/var/lib/otelcol

    restart: "no"
```

For this local learning setup, `0777` avoids depending on the image's internal
numeric UID. In production, initialize the volume with its exact UID/GID and
use narrower permissions.

## 7. Add the Collector container

Add this service to `infra/splunk/docker-compose-splunk.yml`:

```yaml
  primecart-otel-collector:
    image: quay.io/signalfx/splunk-otel-collector:0.150.0
    container_name: primecart-otel-collector

    command:
      - "--config=/etc/otelcol-contrib/config.yaml"

    environment:
      SPLUNK_HEC_TOKEN: "${SPLUNK_HEC_TOKEN}"

    volumes:
      - ./primecart-otel-collector.yaml:/etc/otelcol-contrib/config.yaml:ro
      - /Users/dadaramjadhav/primecart/logs:/var/log/primecart:ro
      - primecart-otel-state:/var/lib/otelcol

    ports:
      - "13134:13133"

    depends_on:
      splunk:
        condition: service_healthy

      primecart-otel-state-init:
        condition: service_completed_successfully

    networks:
      - microservices-network

    restart: unless-stopped
```

Add the named volume:

```yaml
volumes:
  splunk-etc:
    name: primecart-splunk-etc

  splunk-var:
    name: primecart-splunk-var

  primecart-otel-state:
    name: primecart-otel-state
```

The host log directory is mounted read-only. Only the Collector state volume is
writable.

## 8. Validate and start

Validate Compose without printing resolved secret values:

```bash
docker compose \
  --env-file /Users/dadaramjadhav/primecart/infra/splunk/.env \
  -f /Users/dadaramjadhav/primecart/infra/splunk/docker-compose-splunk.yml \
  config --quiet
```

Start Splunk, initialize the state volume, and start the Collector:

```bash
docker compose \
  --env-file /Users/dadaramjadhav/primecart/infra/splunk/.env \
  -f /Users/dadaramjadhav/primecart/infra/splunk/docker-compose-splunk.yml \
  up -d
```

Check every container, including completed one-shot containers:

```bash
docker compose \
  -f /Users/dadaramjadhav/primecart/infra/splunk/docker-compose-splunk.yml \
  ps -a
```

Expected state:

```text
primecart-splunk                       Up (healthy)
primecart-otel-state-init     Exited (0)
primecart-otel-collector      Up
```

`Exited (0)` is correct for the one-time initializer.

Inspect Collector logs:

```bash
docker compose \
  -f /Users/dadaramjadhav/primecart/infra/splunk/docker-compose-splunk.yml \
  logs -f primecart-otel-collector
```

Check Collector health:

```bash
curl -i http://localhost:13134/
```

Expected:

```text
HTTP/1.1 200 OK
```

## 9. Generate and verify new logs

Because `start_at` is `end`, generate a new event after the Collector starts:

```bash
curl http://localhost:8086/actuator/health
```

Call an authenticated profile endpoint through the Gateway:

```text
GET https://localhost:8181/api/customers/me
PUT https://localhost:8181/api/customers/me
```

Open Splunk Search & Reporting:

```text
http://localhost:8000
```

Set the time range to `Last 15 minutes` and search:

```spl
index=main source="customer-service"
```

Inspect parsed fields:

```spl
index=main source="customer-service"
| spath
| table _time service.name severity_text body trace_id span_id
```

If those field names differ, inspect raw events first:

```spl
index=main source="customer-service"
| head 20
```

Summarize received data:

```spl
index=main source="customer-service"
| stats count by sourcetype host
```

Expected metadata includes:

```text
source=customer-service
sourcetype=primecart:json
index=main
```

Splunk may require a browser refresh or a short indexing delay before a newly
received event appears.

## 10. Add application and API logs

Framework startup and shutdown logs prove the pipeline works, but Spring MVC
does not emit a concise access log for every API request at `INFO` by default.

Business logs can be added with Lombok:

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerProfileServiceImpl implements CustomerProfileService {
```

Examples:

```java
log.info("Creating customer profile for keycloakUserId={}", jwt.getSubject());
log.info("Customer profile created. profileId={}", savedProfile.getId());
log.info("Updating customer profile. profileId={}", profile.getId());
```

To produce one access log per application API call, create:

```text
customer-service/src/main/java/com/primecart/filter/HttpRequestLoggingFilter.java
```

```java
package com.primecart.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class HttpRequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        long startedAt = System.nanoTime();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = TimeUnit.NANOSECONDS.toMillis(
                System.nanoTime() - startedAt
            );

            log.info(
                "{} {} -> {} ({} ms)",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                durationMs
            );
        }
    }
}
```

This produces events such as:

```text
GET /api/customers/me -> 200 (31 ms)
PUT /api/customers/me -> 200 (42 ms)
```

The filter deliberately excludes request bodies, query strings, Authorization
headers, and tokens.

Search API access events:

```spl
index=main source="customer-service"
"event.logger_name"="com.primecart.filter.HttpRequestLoggingFilter"
```

Depending on field extraction, the logger might not have the `event` prefix:

```spl
index=main source="customer-service"
logger_name="com.primecart.filter.HttpRequestLoggingFilter"
```

Summarize API status and duration:

```spl
index=main source="customer-service"
"event.logger_name"="com.primecart.filter.HttpRequestLoggingFilter"
| spath
| rex field=event.message "^(?<method>[A-Z]+) (?<path>\S+) -> (?<status>\d{3}) \((?<duration_ms>\d+) ms\)"
| stats count avg(duration_ms) max(duration_ms) by method path status
```

## 11. Complete the ELK-to-Splunk migration

Only remove ELK output after new Customer Service logs reliably appear in
Splunk while Logstash is stopped or unavailable.

Remove this reference from the Customer Service root logger:

```xml
<appender-ref ref="LOGSTASH"/>
```

The final root logger becomes:

```xml
<root level="INFO">
    <appender-ref ref="CONSOLE"/>
    <appender-ref ref="JSON_FILE"/>
</root>
```

Then remove the complete `LOGSTASH` appender block from Customer Service and
restart the service.

The `logstash-logback-encoder` Maven dependency remains necessary because the
JSON rolling-file appender uses it; the dependency name does not mean Logstash
must be running.

## 12. Troubleshooting

### Permission denied under `/var/lib/otelcol`

Error:

```text
open /var/lib/otelcol/exporter_splunk_hec_customer_logs: permission denied
```

Cause: the named volume was created as root, while the Collector runs as a
non-root user.

Fix: retain `primecart-otel-state-init`, confirm it exits with code `0`, and
force-recreate the Collector:

```bash
docker compose \
  --env-file /Users/dadaramjadhav/primecart/infra/splunk/.env \
  -f /Users/dadaramjadhav/primecart/infra/splunk/docker-compose-splunk.yml \
  up -d --force-recreate primecart-otel-state-init primecart-otel-collector
```

### Framework logs appear but API access logs do not

The pipeline is working. Add `HttpRequestLoggingFilter` because Spring MVC does
not produce a concise request completion log at `INFO` by default.

Business logs such as the following are API-related even without an access
filter:

```text
Updating customer profile. profileId=5
```

### Existing logs are not imported

`start_at: end` reads only new lines on the first run. Generate a new request.
To replay a file during a disposable local test, stop the Collector, remove its
file-storage state, and use `start_at: beginning`. Removing state can cause
duplicate ingestion and should not be done casually.

### Splunk shows no logs immediately

1. Set the Splunk search time range to `Last 15 minutes` or `All time`.
2. Refresh the browser.
3. Search `index=main source="customer-service"`.
4. Inspect Collector logs for HEC errors.
5. Confirm `primecart-splunk` resolves on `microservices-network`.

### HEC returns 401

The token is invalid, disabled, or was rotated. Update `infra/splunk/.env`, then
recreate the Collector. Never paste the token into logs or chat.

### Timestamp appears different

Application JSON can contain an ISO-8601 timestamp with an `+05:30` offset or a
UTC `Z` suffix. These can represent the same instant. Configure the Splunk user
time zone as `Asia/Kolkata` and keep event storage timezone-aware.

## 13. Security and production notes

- Rotate any HEC token that has been exposed.
- Use `${env:SPLUNK_HEC_TOKEN}` rather than a hard-coded token.
- Use HTTPS for HEC in production.
- Replace `tls.insecure: true` with certificate validation.
- Replace `chmod 0777` with ownership and permissions for the exact Collector
  UID/GID.
- Avoid logging access tokens, credentials, request bodies, or personal data.
- Keep log directories and `.env` out of Git.
- Pin and deliberately upgrade Collector and Splunk image versions.
- Monitor the Collector health endpoint and sending queue.

## 14. Final verification checklist

- [ ] Customer Service writes `logs/customer-service.json`.
- [ ] JSON events contain `service=customer-service`.
- [ ] Splunk is healthy and HEC is enabled.
- [ ] State initializer exits with code `0`.
- [ ] Customer OTel Collector remains running.
- [ ] `http://localhost:13134/` returns HTTP 200.
- [ ] New Customer Service events appear in `index=main`.
- [ ] Events use `source=customer-service`.
- [ ] Trace and span IDs appear when a request has tracing context.
- [ ] No access tokens, passwords, or personal data are logged.
- [ ] ELK output is removed only after the Splunk path is verified.
