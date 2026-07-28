# PrimeCart Kubernetes Implementation Status

## Summary

PrimeCart has an interview-ready, intermediate Kubernetes implementation for a
local Docker Desktop cluster.

The project demonstrates application deployment, networking, persistence,
availability, security fundamentals, environment overlays, and observability.
It is suitable for explaining and demonstrating Kubernetes concepts in an
interview.

It is not presented as a production-ready platform. Production hardening,
multi-node availability, automated backups, and stricter workload isolation
remain future work.

## Implemented

### Resource organization

- Separate namespaces:
  - `primecart-app`
  - `primecart-infra`
  - `primecart-observe`
- Kubernetes resources organized by application and responsibility.
- Kustomize bases for reusable application definitions.
- Local, development, and production overlay structure.

### Application workloads

- Deployments for stateless Spring Boot services.
- Deployment for the PrimeCart UI.
- StatefulSets for stateful infrastructure.
- Explicit container ports and named Service ports.
- Environment variables for application and infrastructure configuration.
- Secret values loaded with `secretKeyRef`.

### Application availability

- Startup probes.
- Readiness probes.
- Liveness probes.
- Spring Boot graceful shutdown.
- Container `preStop` hooks.
- Pod termination grace periods.
- Rolling-update strategies.
- Rollout progress deadlines.
- Minimum readiness periods.
- Rollout history and rollback procedures.

The local environment currently uses a memory-constrained single-replica
profile:

```yaml
replicas: 1
```

Its rolling update avoids a temporary surge Pod:

```yaml
rollingUpdate:
  maxUnavailable: 1
  maxSurge: 0
```

This local profile may have temporary downtime during updates. The manifests
and documentation explain how a production profile would use multiple replicas
and zero-unavailable rolling updates.

### Resource management and autoscaling

- CPU requests and limits.
- Memory requests and limits.
- Java container resource planning.
- Metrics Server installation and verification.
- HorizontalPodAutoscaler resources.
- CPU-utilization-based HPA configuration.
- PodDisruptionBudget resources and concepts.
- Investigation of a real `Pending` Pod caused by insufficient node memory.

The local HPA is intentionally fixed to one replica to stay within Docker
Desktop capacity:

```yaml
minReplicas: 1
maxReplicas: 1
```

This demonstrates the HPA resource and configuration structure but does not
currently demonstrate live scale-out. A larger environment can increase the
maximum replica count.

### Application networking

- Kubernetes Pod networking.
- ClusterIP Services.
- Stable Service names.
- Same-namespace service discovery.
- Cross-namespace CoreDNS names.
- NGINX Ingress Controller.
- External Ingress routes for the UI, API Gateway, Keycloak, Grafana,
  Prometheus, Spring Boot Admin, application services, RabbitMQ Management and
  Redis Insight.
- Clear separation between internal Service traffic and external Ingress
  traffic.

Examples:

```text
http://product-service:8081
mysql.primecart-infra:3306
otel-collector.primecart-observe:4318
```

### Local HTTPS

- Locally trusted certificates generated with `mkcert`.
- TLS secrets in the namespaces containing Ingress resources.
- HTTPS Ingress configuration.
- HTTP-to-HTTPS redirection.
- HTTPS Keycloak hostname and issuer.
- HTTPS UI and API endpoints.
- Keycloak client redirect URI and web-origin concepts.
- Diagnosis and resolution of an HTTP/HTTPS JWT issuer mismatch.

### Configuration and secrets

- Spring Cloud Config Server backed by a GitHub configuration repository.
- Common external Spring Boot configuration.
- Service-specific external configuration.
- Kubernetes Secrets for credentials.
- Environment-variable overrides.
- Kustomize image overrides by environment.
- Commit-based application image tags in overlays.

### Persistent storage

- StorageClass concepts.
- Docker Desktop `hostpath` StorageClass usage.
- Dynamically provisioned PersistentVolumes.
- PersistentVolumeClaims.
- StatefulSet volume claim templates.
- Persistent storage for MySQL, Redis, RabbitMQ, Grafana, Prometheus, Tempo,
  Logstash and other stateful components.
- Database persistence across Pod restarts.
- Backup and restore procedures documented for selected components.

### Container security fundamentals

- Non-root execution for Spring Boot containers.
- Explicit user and group IDs.
- Disabled privilege escalation.
- Dropped Linux capabilities.
- Read-only root filesystems for backend applications.
- Writable `emptyDir` mounts for required temporary files.
- Kubernetes Secrets instead of plaintext credentials in application
  manifests.

### Metrics and monitoring

- Spring Boot Actuator.
- Health, info and Prometheus endpoints.
- Micrometer Prometheus registry.
- Prometheus Operator and Prometheus.
- ServiceMonitor resources.
- Grafana.
- Alertmanager.
- Kubernetes and infrastructure dashboards supplied by the monitoring stack.
- Metrics Server and `kubectl top`.

### Distributed tracing

- Micrometer Tracing with the OpenTelemetry bridge.
- OTLP trace export.
- OpenTelemetry Collector.
- Tempo trace storage.
- Grafana Tempo datasource.
- Trace propagation across services.
- Filtering of noisy actuator traces.
- Tail-sampling policy that drops complete actuator traces without creating
  orphaned spans.

### Centralized logging and correlation

- Structured JSON application logs.
- Logstash TCP appenders.
- Central Logstash deployment.
- Downstream Splunk integration.
- Trace IDs and span IDs included in log events.
- Documentation for correlating logs and traces.

### Troubleshooting experience captured

Root cause analyses are documented for:

- Grafana failing because multiple datasources were marked as default.
- Actuator traces remaining after an ineffective Java-agent exclusion setting.
- Actuator root-span filtering producing incomplete Tempo traces.
- HTTPS Keycloak issuer mismatch appearing as a browser CORS error.
- Product Service remaining Pending because the local node lacked requested
  memory.

## Partially implemented

### Environment separation

The local, development, and production overlay directories exist, and image
tags differ by overlay. However:

- Several URLs are still defined directly in base manifests.
- Dev and production domains are not fully defined.
- Keycloak issuers are not fully patched per environment.
- UI runtime/build configuration is not completely environment-specific.
- Production resources currently inherit many local values.

### High availability

The manifests demonstrate replicas, rolling updates, HPA, and PDB concepts.
The active local profile uses one replica because the single Docker Desktop
node does not have enough memory for two copies of every service.

A high-availability environment still needs:

- At least two replicas for critical stateless applications.
- `maxUnavailable: 0` and `maxSurge: 1`.
- Meaningful PDB values such as `minAvailable: 1`.
- HPA ranges greater than one.
- Multiple worker nodes.
- Pod anti-affinity or topology spread constraints.

### Grafana application dashboards

Grafana and standard Kubernetes dashboards are available. Dedicated,
provisioned dashboards for every PrimeCart business service are not complete.

### Application alerts

The Prometheus stack includes platform alert rules. Custom application alerts
for error rate, latency, failed business operations, queue depth, inventory
levels and dependency failures are only partially implemented or documented.

### Log-to-trace navigation

Logs contain trace and span IDs, allowing manual correlation. Automatic Grafana
navigation from a log record to the corresponding Tempo trace requires a
supported Grafana log datasource and derived-field configuration.

### Backup and restore

Backup and restore concepts and procedures exist, but the following are not
fully automated:

- Scheduled backups.
- Retention policies.
- Off-cluster backup storage.
- Encryption and access control.
- Regular restore drills.
- Recovery-time and recovery-point validation.

### ServiceAccounts and RBAC

Monitoring components use dedicated ServiceAccounts and RBAC. Most PrimeCart
application Pods still use the namespace's default ServiceAccount.

### Image immutability

Application overlays use explicit commit-based tags. Some base and
infrastructure manifests still use untagged images or `latest`, and images are
not pinned by digest.

## Not implemented or future production work

### Network isolation

- Default-deny NetworkPolicies.
- Explicit application-to-database policies.
- Explicit application-to-RabbitMQ and Redis policies.
- Namespace ingress and egress restrictions.
- DNS egress allowances.

### Production identity and access control

- Dedicated ServiceAccount per application.
- `automountServiceAccountToken: false` where Kubernetes API access is not
  required.
- Least-privilege application RBAC.
- External secret management such as External Secrets Operator, Vault or a
  cloud secret manager.
- Automated secret rotation.

### Production TLS and DNS

- Public DNS.
- Publicly trusted certificates.
- `cert-manager`.
- Automated certificate issuance and renewal.
- Production ingress domains.
- Production TLS policy hardening.

Local HTTPS uses `mkcert`, which is appropriate for development only.

### Production image controls

- Removal of every `latest` and implicit-latest image reference.
- Image digest pinning.
- Image signature verification.
- Admission policies enforcing approved registries and immutable images.
- Automated vulnerability scanning gates.

### Multi-node resilience

- Multi-node Kubernetes cluster testing.
- Topology spread constraints.
- Pod anti-affinity.
- Zone-aware storage.
- Node failure exercises.
- Cluster autoscaling.

### Policy enforcement

- Pod Security Admission enforcement.
- Kyverno or Gatekeeper policies.
- ResourceQuota and LimitRange policies.
- Mandatory labels and ownership policies.
- CI policy checks.

### Disaster recovery automation

- Automated database backups.
- Automated volume snapshots.
- Remote backup replication.
- Full-cluster recovery procedure.
- Scheduled disaster recovery tests.

### Advanced observability

- Complete service-level dashboards.
- Service-level objectives and error budgets.
- Application-specific alert rules and notification routing.
- Automatic Grafana logs-to-traces navigation.
- Production collector scaling and trace-aware load balancing.
- Long-term metrics, logs and trace retention planning.

## Interview readiness

The current project is sufficient for intermediate Kubernetes interviews.
Preparation should now focus on explaining and demonstrating the implementation
rather than adding more features.

Important topics to practise:

1. Deployment versus StatefulSet.
2. Pod, Service and Ingress traffic flow.
3. ClusterIP and CoreDNS service discovery.
4. ConfigMap versus Secret.
5. PV, PVC and StorageClass relationships.
6. Startup, readiness and liveness probe differences.
7. Requests, limits, scheduling and HPA calculations.
8. Rolling updates and rollbacks.
9. PDB behavior and its limitations.
10. Debugging `Pending`, `CrashLoopBackOff`, failed probes and authentication
    errors.
11. Local single-replica trade-offs versus production high availability.
12. Metrics, logs and traces and how they correlate.

## Overall assessment

```text
Local development readiness:  Strong
Interview readiness:          Strong intermediate
Production readiness:         Partial; hardening required
```

The appropriate next step is to stabilize, test, and practise explaining the
existing platform. Additional production features can be implemented later
when they support a concrete deployment requirement.
