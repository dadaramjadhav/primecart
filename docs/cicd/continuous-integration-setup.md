# PrimeCart Continuous Integration Setup

## Overview

PrimeCart uses GitHub Actions to build, analyze, package, scan, and publish all
backend services and the React frontend.

The implemented flow is:

```text
Source change
  -> build
  -> automated tests or linting
  -> SonarQube Cloud analysis
  -> application artifact
  -> Docker image
  -> Trivy vulnerability report
  -> Docker Hub
```

The current implementation provides continuous integration and continuous
delivery of container images. It does not yet deploy those images to a runtime
environment.

## Workflow files

The workflows are stored under:

```text
.github/workflows/
├── reusable-java-service-ci.yml
├── api-gateway-ci.yml
├── cart-service-ci.yml
├── config-server-ci.yml
├── customer-service-ci.yml
├── inventory-service-ci.yml
├── order-service-ci.yml
├── payment-service-ci.yml
├── product-service-ci.yml
├── sb-admin-server-ci.yml
└── primecart-ui-ci.yml
```

## Workflow coverage

| Application | Workflow | Docker Hub repository |
|---|---|---|
| API Gateway | `api-gateway-ci.yml` | `primecart-api-gateway` |
| Cart Service | `cart-service-ci.yml` | `primecart-cart-service` |
| Config Server | `config-server-ci.yml` | `primecart-config-server` |
| Customer Service | `customer-service-ci.yml` | `primecart-customer-service` |
| Inventory Service | `inventory-service-ci.yml` | `primecart-inventory-service` |
| Order Service | `order-service-ci.yml` | `primecart-order-service` |
| Payment Service | `payment-service-ci.yml` | `primecart-payment-service` |
| Product Service | `product-service-ci.yml` | `primecart-product-service` |
| Spring Boot Admin | `sb-admin-server-ci.yml` | `primecart-sb-admin-server` |
| PrimeCart UI | `primecart-ui-ci.yml` | `primecart-ui` |

## Workflow triggers

Each application workflow supports:

- Pull requests targeting `main`.
- Pushes to `main`.
- Manual execution using `workflow_dispatch`.

Path filters ensure that only affected applications run. For example, a change
under `inventory-service/**` triggers Inventory Service CI but does not trigger
Product Service CI.

When `reusable-java-service-ci.yml` changes, every Java caller containing that
path filter runs because the shared implementation may affect every service.

The workflows use concurrency groups:

```yaml
concurrency:
  group: <application>-ci-${{ github.ref }}
  cancel-in-progress: true
```

When several commits are pushed quickly to one branch, an older in-progress run
is cancelled in favor of the newest commit.

## Reusable Java service workflow

All Java applications call:

```text
.github/workflows/reusable-java-service-ci.yml
```

This prevents Maven, SonarQube, artifact, Docker, Trivy, and Docker Hub logic
from being copied into every service workflow.

### Reusable inputs

| Input | Purpose |
|---|---|
| `service_directory` | Directory containing `pom.xml`, Maven Wrapper, and Dockerfile |
| `service_name` | Name used for local images, jobs, and artifacts |
| `dockerhub_image_name` | Docker Hub repository without the account name |
| `dockerhub_username` | Docker Hub user or organization |
| `sonar_project_key` | Service-specific SonarQube Cloud project key |
| `sonar_organization` | Shared SonarQube Cloud organization |
| `java_version` | Java version; currently `17` |

### Reusable secrets

| Secret | Purpose |
|---|---|
| `SONAR_TOKEN` | Authenticates SonarQube Cloud analysis |
| `DOCKERHUB_TOKEN` | Authenticates image publishing to Docker Hub |

The secrets are passed explicitly by each caller. They are never stored in the
workflow, application configuration, Dockerfile, or repository.

### Java build job

The `build-and-test` job:

1. Checks out the complete Git history using `fetch-depth: 0`.
2. Configures Temurin Java 17.
3. Restores the Maven dependency cache.
4. Makes the service Maven Wrapper executable.
5. Runs `clean verify`.
6. Performs SonarQube Cloud analysis.
7. Uploads Surefire reports when they exist.
8. Uploads the packaged application JAR.

The Maven command is:

```bash
./mvnw \
  --batch-mode \
  --no-transfer-progress \
  clean verify \
  org.sonarsource.scanner.maven:sonar-maven-plugin:5.5.0.6356:sonar
```

Sonar analysis and Maven verification run in the same command so the scanner
can use the complete build context.

### Fork pull requests

GitHub does not provide repository secrets to untrusted fork pull requests.
The workflow therefore performs Maven verification without SonarQube for fork
pull requests:

```text
Same-repository branch or main push
  -> Maven verify and SonarQube analysis

Fork pull request
  -> Maven verify without SONAR_TOKEN
```

### Java container job

The `container-build-and-scan` job depends on `build-and-test`.

It:

1. Downloads the JAR produced by the first job.
2. Builds the runtime image without rebuilding the Java application.
3. Inspects the image size and configured runtime user.
4. Generates a Trivy JSON vulnerability report.
5. Uploads the report as a GitHub Actions artifact.
6. Prints fixable critical findings in the job log.
7. Publishes the image to Docker Hub after a push to `main`.

This follows:

```text
Build once -> scan the image -> publish the same image
```

## PrimeCart UI workflow

The React/Vite frontend has a dedicated workflow because it uses Node rather
than Maven.

The UI build job:

1. Checks out the complete Git history.
2. Configures Node.js 22.
3. Restores the npm cache.
4. Installs exact lockfile dependencies using `npm ci`.
5. Runs ESLint.
6. Runs the Vite production build.
7. Performs SonarQube Cloud analysis of `primecart-ui/src`.
8. Uploads `primecart-ui/dist` as an artifact.

The UI does not currently define an automated test script. Tests can be added
later without changing the container publishing design.

### Vite development TLS

The UI uses local certificates for the Vite development server. The Vite
configuration loads these certificates only for the `serve` command.

```text
npm run dev
  -> uses local development TLS files

npm run build
  -> does not load local TLS files
```

This prevents GitHub Actions from requiring or receiving the local private key.
Production TLS will be terminated by the deployment platform, such as an
Ingress, load balancer, or CloudFront.

The UI container job:

1. Builds the multi-stage Node/Nginx image.
2. Creates and uploads a Trivy report.
3. Displays fixable critical findings.
4. Publishes the image to Docker Hub after a push to `main`.

## SonarQube Cloud configuration

Each application has its own SonarQube Cloud project and project key. This
prevents one service analysis from overwriting another service.

Configured GitHub variables include:

```text
SONAR_ORGANIZATION
SONAR_PROJECT_KEY
SONAR_API_GATEWAY_PROJECT_KEY
SONAR_CART_SERVICE_PROJECT_KEY
SONAR_CONFIG_SERVER_PROJECT_KEY
SONAR_CUSTOMER_SERVICE_PROJECT_KEY
SONAR_INVENTORY_SERVICE_PROJECT_KEY
SONAR_ORDER_SERVICE_PROJECT_KEY
SONAR_PAYMENT_SERVICE_PROJECT_KEY
SONAR_SB_ADMIN_SERVER_PROJECT_KEY
SONAR_UI_PROJECT_KEY
```

`SONAR_PROJECT_KEY` is currently used by Product Service. It can later be
renamed to `SONAR_PRODUCT_SERVICE_PROJECT_KEY` for naming consistency.

Automatic analysis must be disabled for projects analyzed by GitHub Actions.
Each project uses CI-based analysis and its service directory as the project
base directory.

The current Java configuration uses:

```text
sonar.qualitygate.wait=false
```

SonarQube records and displays issues, but its Quality Gate does not currently
block image creation or publishing.

## Trivy vulnerability scanning

Every generated application image is scanned for:

```text
UNKNOWN
LOW
MEDIUM
HIGH
CRITICAL
```

Both operating-system and application-library packages are included:

```yaml
vuln-type: os,library
```

The complete JSON report is retained for 14 days as a workflow artifact.

The current policy uses:

```yaml
exit-code: "0"
```

Therefore, vulnerabilities of any severity are reported but do not fail the
pipeline. Build failures, test failures, lint failures, Docker build failures,
authentication errors, and publishing errors can still fail the pipeline.

Once the vulnerability baseline is reviewed, the policy can be tightened to
block fixable critical findings.

## Docker Hub publishing

Docker Hub authentication and publishing occur only when:

```yaml
github.event_name == 'push' && github.ref == 'refs/heads/main'
```

Pull requests build and scan images without receiving Docker Hub credentials
and without publishing.

Each successful main-branch build publishes two tags:

```text
<dockerhub-user>/<application>:<git-commit-sha>
<dockerhub-user>/<application>:main
```

The Git SHA tag identifies an exact source revision and should be used for
deployment and rollback.

The `main` tag is a mutable convenience pointer to the newest successful image
created from the main branch. Production deployments should not rely only on
the `main` tag.

## GitHub variables

The repository uses the following shared variables:

```text
DOCKERHUB_USERNAME
SONAR_ORGANIZATION
```

It also uses one SonarQube project-key variable per application, as listed
earlier.

Variables contain identifiers and names, not credentials.

## GitHub secrets

The repository uses:

```text
DOCKERHUB_TOKEN
SONAR_TOKEN
```

`DOCKERHUB_TOKEN` must be a Docker Hub personal access token with the minimum
permissions required to publish images. The Docker Hub account password must
not be used in CI.

Secrets must never be committed, printed, uploaded as artifacts, embedded in
images, or exposed as frontend Vite variables.

## Docker build contexts

Every application has a service-level `.dockerignore`. The root
`.dockerignore` is not used when a service directory is the Docker build
context.

Java service Docker contexts retain only the runtime inputs:

```dockerignore
*
!Dockerfile
!target/
!target/*.jar
```

The frontend excludes local dependencies, build output, environment files,
certificates, IDE files, documentation, and temporary files.

## Artifact retention

| Artifact | Retention |
|---|---:|
| Test reports | 7 days |
| Packaged JAR | 7 days |
| Frontend `dist` build | 7 days |
| Trivy vulnerability report | 14 days |

Artifacts help diagnose failed builds and preserve evidence of what the
pipeline generated.

## Current status

Implemented:

- Path-filtered workflows for all applications.
- Shared Java workflow.
- Maven and npm dependency caching.
- Backend verification.
- Frontend linting and production build.
- SonarQube Cloud analysis.
- JAR and frontend artifact uploads.
- Docker image builds.
- Trivy reports.
- Immutable Git SHA image tags.
- Docker Hub publishing from `main`.
- Non-root Java runtime images.

Not yet implemented:

- A monorepo coordinator status check.
- Enforced SonarQube Quality Gates.
- Vulnerability-based pipeline failure.
- Consistent test coverage thresholds.
- Frontend automated tests.
- Full integration tests.
- Deployment to Kubernetes, ECS, or EKS.
- Post-deployment health and business smoke tests.
- Automatic deployment rollback.

## Continuous delivery boundary

The current automation ends at the container registry:

```text
GitHub main branch
  -> verified and analyzed source
  -> scanned Docker image
  -> Docker Hub SHA tag
```

The next stage requires a deployment target. After Kubernetes or AWS
infrastructure is available, a deployment workflow can:

1. Select the immutable Git SHA image.
2. Update the target deployment.
3. Wait for rollout completion.
4. Check application health.
5. Run a controlled smoke test.
6. Roll back to the previous image when verification fails.

Until that stage is implemented, the project has continuous integration and
container-image delivery, but not continuous deployment.
