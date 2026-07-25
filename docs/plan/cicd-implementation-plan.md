# PrimeCart CI/CD Implementation Plan

## Objective

Build a secure CI/CD pipeline for the PrimeCart monorepo that validates every
change, creates immutable container images, deploys them safely, verifies the
deployment, and supports rollback.

The implementation will be incremental:

```text
Feature branch
  -> Pull request
  -> Detect changed services
  -> Compile and test
  -> Security scanning
  -> Build Docker image
  -> Vulnerability scan
  -> Merge to main
  -> Push versioned image
  -> Deploy
  -> Smoke test
  -> Roll back on failure
```

## Phase 1: Prepare the repository

### Branch workflow

Use the following branch convention:

```text
main
feature/<feature-name>
fix/<bug-name>
```

Changes should reach `main` through pull requests.

Configure branch protection for `main`:

- Require a pull request.
- Require successful CI checks.
- Prevent force pushes.
- Require the branch to be up to date before merging.
- Enable GitHub push protection.
- Optionally require one approval.

### Standardize application builds

Every Java service must successfully run:

```bash
./mvnw clean verify
```

The frontend must successfully run:

```bash
npm ci
npm run lint
npm run build
```

Each service should contain:

- Maven Wrapper for backend services.
- Tests that do not require manually started infrastructure.
- A Dockerfile and `.dockerignore`.
- An Actuator health endpoint.
- No credentials in source code or test configuration.

### Container image naming

Use a separate image repository for every deployable application:

```text
primecart/api-gateway
primecart/cart-service
primecart/config-server
primecart/customer-service
primecart/inventory-service
primecart/order-service
primecart/payment-service
primecart/product-service
primecart/admin-server
primecart/ui
```

Use the Git commit SHA as the immutable image tag:

```text
product-service:a73bd21
```

Optional convenience tags can be published alongside it:

```text
product-service:main
product-service:v1.2.0
```

Deployments must not depend only on `latest`.

## Phase 2: Implement the first CI workflows

Create:

```text
.github/
└── workflows/
    ├── backend-ci.yml
    └── frontend-ci.yml
```

Trigger CI for:

- Pull requests targeting `main`.
- Pushes to `main`.
- Manual runs using `workflow_dispatch`.

### Backend CI

Initially use a matrix containing:

```text
api-gateway
cart-service
config-server
customer-service
inventory-service
order-service
payment-service
product-service
sb-admin-server
```

Each matrix job should:

1. Check out the repository.
2. Install Java 17.
3. Enable Maven dependency caching.
4. Run `./mvnw clean verify`.
5. Upload test reports when tests fail.
6. Upload the packaged JAR as a build artifact.

### Frontend CI

The frontend job should:

1. Install Node.
2. Enable npm dependency caching.
3. Run `npm ci`.
4. Run linting.
5. Run automated tests when available.
6. Run the production build.
7. Upload `dist/` as a build artifact.

### Phase 2 completion criteria

- Java compilation failures block pull requests.
- Test failures block pull requests.
- React lint or build failures block pull requests.
- Maven and npm dependencies are cached.
- The workflow does not require Docker registry or cloud credentials.

## Phase 3: Optimize for the monorepo

After the initial workflows are stable, detect changed services:

```text
product-service/** changed
    -> test product-service

primecart-ui/** changed
    -> test frontend

devops/** changed
    -> validate the relevant deployment configuration

.github/workflows/** changed
    -> run all applicable checks
```

Also run dependent service checks when shared contracts or configuration
change. Begin with the complete service matrix and introduce optimization only
after the basic workflow is reliable.

## Phase 4: Add security checks

Create:

```text
.github/workflows/security.yml
```

### Source and dependency scanning

- GitHub secret scanning and push protection.
- Dependabot dependency updates.
- Maven dependency vulnerability scanning.
- npm dependency auditing.
- CodeQL scanning for Java and JavaScript.
- Trivy filesystem scanning.

### Container scanning

For every changed service:

1. Build the Docker image.
2. Scan the final image using Trivy.
3. Fail for fixable `CRITICAL` vulnerabilities.
4. Initially report existing `HIGH` findings without failing if a large
   baseline exists.
5. Upload a SARIF report to GitHub Security.

Tighten vulnerability thresholds after documenting and reducing the initial
baseline.

### Workflow security

- Give each job the minimum required GitHub permissions.
- Never print credentials or tokens.
- Do not expose secrets to untrusted pull requests.
- Pin third-party actions to commit SHAs for hardened workflows.
- Use OpenID Connect for AWS access instead of permanent AWS access keys.

## Phase 5: Build and publish container images

Create:

```text
.github/workflows/publish-images.yml
```

Run this workflow after a successful merge into `main`.

The workflow should:

1. Determine which services changed.
2. Require successful CI.
3. Build each changed image.
4. Scan the final image.
5. Authenticate to the container registry.
6. Push the image using the Git SHA tag.
7. Record the image digest and scan report.

Example:

```text
123456789012.dkr.ecr.ap-south-1.amazonaws.com/product-service:a73bd21
sha256:<image-digest>
```

GitHub Container Registry can be used before AWS is available. The destination
can later be changed to Amazon ECR.

## Phase 6: Configure deployment environments

Create the following GitHub environments:

```text
development
staging
production
```

| Environment | Trigger | Approval |
|---|---|---|
| Development | Merge into `main` | Automatic |
| Staging | Version tag or manual run | Optional |
| Production | Manual promotion | Required |

Use environments to restrict deployment branches, protect secrets, retain
deployment history, and require approval.

Build an image once and promote the exact same digest:

```text
Build once -> scan once -> deploy the same digest everywhere
```

Do not rebuild a separate image for production.

## Phase 7: Prepare local Kubernetes delivery

Use a structure such as:

```text
kubernetes/
├── base/
└── overlays/
    ├── local/
    ├── development/
    └── production/
```

Initially, keep local deployment manual:

```bash
kubectl apply -k kubernetes/overlays/local
kubectl rollout status deployment/product-service
```

CI should validate:

- Kubernetes YAML syntax.
- Kustomize rendering.
- Resource requests and limits.
- Readiness and liveness probes.
- Absence of plaintext secrets.

A laptop-based self-hosted GitHub runner can be used temporarily for learning,
but it should not be presented as the production deployment model.

## Phase 8: Add AWS deployment

Use this authentication and deployment flow:

```text
GitHub Actions
  -> GitHub OIDC
  -> temporary AWS IAM role
  -> Amazon ECR
  -> ECS or EKS deployment
```

Do not store long-lived AWS access keys as GitHub secrets.

AWS deployment steps:

1. Authenticate to AWS through OIDC.
2. Push the immutable image to ECR.
3. Update the ECS task definition or Kubernetes image.
4. Wait for rollout completion.
5. Verify Actuator health endpoints.
6. Run a business smoke test.
7. Mark the deployment successful.
8. Roll back when verification fails.

## Phase 9: Add smoke tests and rollback

Begin with basic checks:

```text
GET /actuator/health
GET /api/products
GET /api/products/{known-id}
```

Later, validate a controlled business flow:

```text
Create test product
  -> verify inventory
  -> place test order
  -> verify order state
  -> clean up test data
```

Rollback options:

- Kubernetes: restore the previous Deployment revision.
- ECS: restore the previous task-definition revision.
- Database: use backward-compatible Flyway migrations.

Application rollback must not be expected to reverse a destructive database
migration automatically.

## Suggested implementation schedule

### Week 1

- Add backend matrix CI.
- Add frontend CI.
- Fix builds and tests until the workflows are stable.
- Protect the `main` branch.
- Add Maven and npm caching.

### Week 2

- Add Docker image builds.
- Add Trivy container scanning.
- Add dependency and secret scanning.
- Add changed-service detection.
- Publish immutable images to a registry.

### After Kubernetes or AWS is ready

- Add development deployment.
- Verify rollout status.
- Add smoke tests.
- Add staging and production environments.
- Add deployment approval and rollback.

## Definition of done

The CI/CD implementation is portfolio-ready when it demonstrates:

- Compilation and test failures blocking pull requests.
- Only affected services being rebuilt.
- Cached Maven and npm dependencies.
- Docker vulnerability scanning.
- Immutable image tags and recorded digests.
- No long-lived AWS credentials.
- Automatic development deployment.
- Manual production approval.
- Health and business smoke tests.
- Rollback to the previous application image.
- Visible deployment history.

## Immediate next step

Implement only:

```text
.github/workflows/backend-ci.yml
.github/workflows/frontend-ci.yml
```

Deployment should be added after these workflows remain reliable across
multiple pull requests.

## References

- [Building and testing Java with Maven](https://docs.github.com/en/actions/tutorials/build-and-test-code/java-with-maven)
- [Dependency caching reference](https://docs.github.com/en/actions/reference/workflows-and-actions/dependency-caching)
- [GitHub deployment environments](https://docs.github.com/en/actions/reference/workflows-and-actions/deployments-and-environments)
- [Configuring OpenID Connect in AWS](https://docs.github.com/en/actions/how-tos/secure-your-work/security-harden-deployments/oidc-in-aws)
