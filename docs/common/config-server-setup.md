# Spring Cloud Config Server Setup

## 1. Overview

PrimeCart uses Spring Cloud Config Server to provide centralized configuration to its microservices. The Config Server:

- Runs on port `8888`
- Reads configuration from the `main` branch of the private `primecart-config` Git repository
- Registers with Spring Boot Admin at `http://localhost:9191`
- Exposes Actuator health, info, and Prometheus endpoints

The Config Server source is located in the `config-server` module.

## 2. Prerequisites

Install or configure the following before starting the server:

- Java 17
- Maven, or use the included Maven wrapper
- Access to the PrimeCart configuration repository
- A GitHub personal access token with read access to that repository
- Spring Boot Admin running on port `9191` if application monitoring is required

## 3. Required Environment Variables

The application reads credentials from environment variables. Do not place their literal values in `application.yaml` or commit them to Git.

| Variable | Purpose |
| --- | --- |
| `GITHUB_ACCESS_TOKEN` | Authenticates the Config Server when cloning the private configuration repository |
| `SBA_PASSWORD` | Authenticates the Config Server client with Spring Boot Admin |

Set the variables in the terminal session used to start the application:

```bash
export GITHUB_ACCESS_TOKEN="your-new-token"
export SBA_PASSWORD="your-admin-password"
```

Use a newly generated token if a previous token was committed or exposed. Revoking the exposed token is required even after it has been removed from Git history.

## 4. Config Server Configuration

The configuration is stored at `config-server/src/main/resources/application.yaml`:

```yaml
server:
  port: 8888

spring:
  application:
    name: config-server

  boot:
    admin:
      client:
        url: http://localhost:9191
        username: admin
        password: ${SBA_PASSWORD}

  cloud:
    config:
      server:
        git:
          uri: https://github.com/dadaramjadhav/primecart-config.git
          username: dadaramjadhav
          password: ${GITHUB_ACCESS_TOKEN}
          default-label: main
          clone-on-start: true
          timeout: 10

management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus
  endpoint:
    health:
      show-details: always
```

`clone-on-start` makes configuration-repository or authentication errors visible during startup instead of waiting for the first client request.

## 5. Enable Config Server

The application class enables Spring Cloud Config Server with `@EnableConfigServer`:

```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

The module includes the `spring-cloud-config-server`, Spring Boot Actuator, Prometheus, and Spring Boot Admin client dependencies.

## 6. Start the Server

From the repository root, run:

```bash
cd config-server
./mvnw spring-boot:run
```

Alternatively, start `ConfigServerApplication` from the IDE and add `GITHUB_ACCESS_TOKEN` and `SBA_PASSWORD` to the run configuration's environment variables.

Start the Config Server before starting microservices that import remote configuration.

## 7. Configure Config Clients

PrimeCart services connect to the server using `spring.config.import`:

```yaml
spring:
  config:
    import: configserver:http://localhost:8888
```

This import is currently used by the API Gateway and the customer, product, cart, inventory, order, and payment services.

If a service must be allowed to start temporarily when the Config Server is unavailable, use the optional form:

```yaml
spring:
  config:
    import: optional:configserver:http://localhost:8888
```

The optional form should only be used when starting with local or default configuration is acceptable.

## 8. Verify the Setup

Check the Config Server health endpoint:

```bash
curl http://localhost:8888/actuator/health
```

Request configuration for an application and profile:

```bash
curl http://localhost:8888/product-service/default
```

To request a specific Git label, use:

```bash
curl http://localhost:8888/product-service/default/main
```

A successful response contains the application name, active profiles, Git label, and property sources loaded from the configuration repository.

## 9. Troubleshooting

### Config repository authentication fails

- Confirm `GITHUB_ACCESS_TOKEN` exists in the same environment that launches the application.
- Confirm the token has read access to the private configuration repository.
- Confirm the token has not expired or been revoked.
- Never bypass GitHub push protection for a real token committed to source control.

### A client cannot connect

- Confirm the Config Server is listening on port `8888`.
- Confirm the client uses `configserver:http://localhost:8888`.
- In containers, replace `localhost` with the Config Server service or container hostname.
- Check the Config Server logs for a missing application or profile configuration.

### Spring Boot Admin registration fails

- Confirm Spring Boot Admin is available at `http://localhost:9191`.
- Confirm `SBA_PASSWORD` matches the Admin Server password.
- If Spring Boot Admin is not required during local development, disable its client registration in the local configuration.

## 10. Secret Handling

- Keep `.env` and secret files excluded through `.gitignore`.
- Store production secrets in the deployment platform or a secret manager such as Vault.
- Commit variable references such as `${GITHUB_ACCESS_TOKEN}`, never their resolved values.
- Rotate any credential that appears in a commit, terminal recording, build log, or shared message.
- Removing a secret from the latest file is insufficient; it must also be removed from every unpushed commit that contains it.
