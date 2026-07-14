# Spring Boot Admin Setup Guide

## 1. Overview

Spring Boot Admin provides a web-based dashboard for monitoring and managing Spring Boot applications.

It uses Spring Boot Actuator endpoints to display information such as:

- Application health
- Application status
- JVM memory usage
- CPU usage
- Threads
- Environment properties
- Application logs
- HTTP request metrics
- Scheduled tasks
- Cache information
- Beans
- Configuration properties

Spring Boot Admin consists of two parts:

1. **Spring Boot Admin Server**
2. **Spring Boot Admin Client**

---

## 2. Architecture

```text
Product Service ───────┐
Order Service ─────────┤
Cart Service ──────────┤
Inventory Service ─────┼──> Spring Boot Admin Server
Payment Service ───────┤          Port: 9091
API Gateway ───────────┘
```

Each microservice registers itself with the Spring Boot Admin Server.

The Admin Server reads monitoring information from the service's Actuator endpoints.

---

## 3. Create Spring Boot Admin Server

Create a separate Spring Boot application named:

```text
spring-boot-admin-server
```

Recommended port:

```text
9091
```

---

## 4. Admin Server Dependencies

Add the following dependency to the Admin Server `pom.xml`.

```xml

<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-server</artifactId>
</dependency>

<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Use a Spring Boot Admin version compatible with your Spring Boot version.

Example dependency management:

```xml

<properties>
    <java.version>17</java.version>
    <spring-boot-admin.version>3.5.1</spring-boot-admin.version>
</properties>

<dependencyManagement>
<dependencies>
    <dependency>
        <groupId>de.codecentric</groupId>
        <artifactId>spring-boot-admin-dependencies</artifactId>
        <version>${spring-boot-admin.version}</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
</dependencies>
</dependencyManagement>
```

---

## 5. Enable Spring Boot Admin Server

Add `@EnableAdminServer` to the main application class.

```java
package com.primecart.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAdminServer
@SpringBootApplication
public class SpringBootAdminServerApplication {

    static void main(String[] args) {
        SpringApplication.run(SpringBootAdminServerApplication.class, args);
    }
}
```

---

## 6. Admin Server Configuration

Create `application.yml`.

```yaml
server:
  port: 9091

spring:
  application:
    name: spring-boot-admin-server

  security:
    user:
      name: admin
      password: admin123

management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: always
```

---

## 7. Admin Server Security Configuration

Create a security configuration for the Admin Server.

```java
package com.primecart.admin.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.util.UUID;

@Configuration
public class SecurityConfig {

    private final AdminServerProperties adminServerProperties;

    public SecurityConfig(AdminServerProperties adminServerProperties) {
        this.adminServerProperties = adminServerProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        String adminContextPath = adminServerProperties.getContextPath();

        SavedRequestAwareAuthenticationSuccessHandler successHandler =
                new SavedRequestAwareAuthenticationSuccessHandler();

        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(adminContextPath + "/");

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                adminContextPath + "/assets/**",
                                adminContextPath + "/login",
                                adminContextPath + "/instances"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage(adminContextPath + "/login")
                        .successHandler(successHandler)
                )
                .logout(logout -> logout
                        .logoutUrl(adminContextPath + "/logout")
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf
                        .csrfTokenRepository(
                                org.springframework.security.web.csrf.CookieCsrfTokenRepository
                                        .withHttpOnlyFalse()
                        )
                        .ignoringRequestMatchers(
                                adminContextPath + "/instances",
                                adminContextPath + "/instances/*"
                        )
                )
                .rememberMe(rememberMe -> rememberMe
                        .key(UUID.randomUUID().toString())
                        .tokenValiditySeconds(1209600)
                );

        return http.build();
    }
}
```

> For local development, the default username and password are acceptable.  
> For production, store credentials in environment variables or a secret manager.

---

## 8. Add Spring Boot Admin Client to Microservices

Add the following dependency to every microservice that should appear in the Admin dashboard.

```xml

<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-client</artifactId>
</dependency>

<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Add these dependencies to:

- API Gateway
- Product Service
- Order Service
- Cart Service
- Inventory Service
- Payment Service

---

## 9. Client Configuration for Local Development

Add the following configuration to each microservice.

Example for Product Service:

```yaml
spring:
  application:
    name: product-service

  boot:
    admin:
      client:
        url: http://localhost:9091
        username: admin
        password: admin123
        instance:
          service-url: http://localhost:8081
          management-url: http://localhost:8081/actuator
          health-url: http://localhost:8081/actuator/health
          metadata:
            user.name: admin
            user.password: admin123

management:
  endpoints:
    web:
      exposure:
        include: "*"

  endpoint:
    health:
      show-details: always

  info:
    env:
      enabled: true
```

Change the service URL and port for every service.

| Service           | Port |
|-------------------|-----:|
| API Gateway       | 8080 |
| Product Service   | 8081 |
| Order Service     | 8082 |
| Cart Service      | 8083 |
| Inventory Service | 8084 |
| Payment Service   | 8085 |
| Spring Boot Admin | 9091 |

---

## 10. Common Client Configuration

A simpler configuration can be used when Spring Boot Admin and the services can directly reach each other.

```yaml
spring:
  boot:
    admin:
      client:
        url: http://localhost:9091
        username: admin
        password: admin123

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,loggers,env,beans,caches,threaddump,heapdump

  endpoint:
    health:
      show-details: always

  info:
    env:
      enabled: true
```

---

## 11. Actuator Security Configuration

If Spring Security is enabled in a microservice, allow Spring Boot Admin to access the required Actuator endpoints.

Example for a Servlet-based Spring Boot application:

```java
package com.primecart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/info"
                        ).permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(Customizer.withDefaults())
                );

        return http.build();
    }
}
```

For a simple local setup, all Actuator endpoints can temporarily be permitted:

```java
.requestMatchers("/actuator/**").

permitAll()
```

Do not expose all Actuator endpoints publicly in production.

---

## 12. API Gateway WebFlux Security

For Spring Cloud Gateway, use `SecurityWebFilterChain`.

```java
package com.primecart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http) {

        return http
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(
                                "/actuator/health",
                                "/actuator/info"
                        ).permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(Customizer.withDefaults())
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }
}
```

---

## 13. Application Information

Add application information to each service.

```yaml
info:
  app:
    name: ${spring.application.name}
    description: PrimeCart microservice
    version: 1.0.0
```

The information appears in Spring Boot Admin under the application details.

Maven project information can also be exposed.

```xml

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

---

## 14. Show Git Information

Add the Git commit plugin.

```xml

<plugin>
    <groupId>io.github.git-commit-id</groupId>
    <artifactId>git-commit-id-maven-plugin</artifactId>
</plugin>
```

Enable Git information:

```yaml
management:
  info:
    git:
      mode: full
```

Spring Boot Admin can then display:

- Commit ID
- Branch
- Commit time
- Build information

---

## 15. Log File Monitoring

Spring Boot Admin can display the application log file.

Add:

```yaml
logging:
  file:
    name: logs/${spring.application.name}.log
```

Expose the log file endpoint:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,logfile,loggers
```

The Admin dashboard will display a **Logfile** section.

---

## 16. Docker Configuration

When Spring Boot Admin runs in Docker, do not use `localhost` for communication between containers.

Use Docker service names.

Example client configuration:

```yaml
spring:
  boot:
    admin:
      client:
        url: http://spring-boot-admin:9091
        username: admin
        password: admin123

        instance:
          service-url: http://product-service:8081
          management-url: http://product-service:8081/actuator
          health-url: http://product-service:8081/actuator/health
```

---

## 17. Docker Compose Example

```yaml
services:

  spring-boot-admin:
    image: dadaramjadhav/spring-boot-admin:latest
    container_name: spring-boot-admin
    ports:
      - "9091:9091"
    environment:
      SPRING_SECURITY_USER_NAME: admin
      SPRING_SECURITY_USER_PASSWORD: admin123
    networks:
      - microservices-network
    restart: unless-stopped

  product-service:
    image: dadaramjadhav/product-service:latest
    container_name: product-service
    ports:
      - "8081:8081"
    environment:
      SPRING_BOOT_ADMIN_CLIENT_URL: http://spring-boot-admin:9091
      SPRING_BOOT_ADMIN_CLIENT_USERNAME: admin
      SPRING_BOOT_ADMIN_CLIENT_PASSWORD: admin123
      SPRING_BOOT_ADMIN_CLIENT_INSTANCE_SERVICE_URL: http://product-service:8081
      SPRING_BOOT_ADMIN_CLIENT_INSTANCE_MANAGEMENT_URL: http://product-service:8081/actuator
      SPRING_BOOT_ADMIN_CLIENT_INSTANCE_HEALTH_URL: http://product-service:8081/actuator/health
    depends_on:
      - spring-boot-admin
    networks:
      - microservices-network
    restart: unless-stopped

networks:
  microservices-network:
    external: true
```

---

## 18. Build Docker Image for Admin Server

Example `Dockerfile`:

```dockerfile
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/spring-boot-admin-server.jar app.jar

EXPOSE 9091

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build the application:

```bash
mvn clean package -DskipTests
```

Build the Docker image:

```bash
docker build -t dadaramjadhav/spring-boot-admin:latest .
```

Push the image:

```bash
docker push dadaramjadhav/spring-boot-admin:latest
```

---

## 19. Start the Applications

Start the Admin Server first:

```bash
mvn spring-boot:run
```

Then start the microservices.

Open the dashboard:

```text
http://localhost:9091
```

Login using:

```text
Username: admin
Password: admin123
```

---

## 20. Verify Client Registration

Check the microservice logs.

A successful registration usually shows that the application was registered with Spring Boot Admin.

Verify the following endpoint manually:

```text
http://localhost:8081/actuator/health
```

Expected response:

```json
{
  "status": "UP"
}
```

---

## 21. Useful Actuator Endpoints

| Endpoint                   | Purpose                      |
|----------------------------|------------------------------|
| `/actuator/health`         | Application health           |
| `/actuator/info`           | Application information      |
| `/actuator/metrics`        | Application metrics          |
| `/actuator/prometheus`     | Prometheus metrics           |
| `/actuator/loggers`        | View and change log levels   |
| `/actuator/env`            | Environment properties       |
| `/actuator/beans`          | Spring beans                 |
| `/actuator/caches`         | Cache information            |
| `/actuator/threaddump`     | Thread dump                  |
| `/actuator/heapdump`       | Heap dump                    |
| `/actuator/scheduledtasks` | Scheduled task information   |
| `/actuator/mappings`       | Controller endpoint mappings |

---

## 22. Recommended Endpoint Exposure

For local development:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

For production:

```yaml
management:
  endpoints:
    web:
      exposure:
        include:
          - health
          - info
          - metrics
          - prometheus
          - loggers
          - caches
```

Avoid exposing sensitive endpoints such as `env`, `heapdump`, and `configprops` without authentication.

---

## 23. Production Recommendations

For a production-ready setup:

1. Protect the Admin Server using Spring Security.
2. Use HTTPS.
3. Store credentials in environment variables or a secret manager.
4. Restrict Actuator endpoints.
5. Keep the Admin Server inside a private network.
6. Configure authentication between Admin Server and clients.
7. Configure notification alerts.
8. Use persistent service discovery for dynamic environments.
9. Avoid exposing heap dumps publicly.
10. Configure health groups for readiness and liveness.
11. Run more than one Admin Server instance if high availability is required.
12. Integrate with Prometheus and Grafana for long-term metrics.
13. Use ELK or Loki for centralized log storage.
14. Use OpenTelemetry and Tempo for distributed tracing.

---

## 24. Notification Configuration

Spring Boot Admin supports notifications for application status changes.

Supported integrations include:

- Email
- Microsoft Teams
- Slack
- Telegram
- PagerDuty
- Discord
- OpsGenie

Example email notification configuration:

```yaml
spring:
  boot:
    admin:
      notify:
        mail:
          enabled: true
          to: operations@example.com
          from: spring-boot-admin@example.com

  mail:
    host: smtp.example.com
    port: 587
    username: smtp-user
    password: smtp-password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

---

## 25. Spring Boot Admin vs Prometheus and Grafana

Spring Boot Admin and Prometheus/Grafana solve different monitoring requirements.

| Tool              | Primary Purpose                                   |
|-------------------|---------------------------------------------------|
| Spring Boot Admin | Live operational view of Spring Boot applications |
| Prometheus        | Collect and store time-series metrics             |
| Grafana           | Visualize metrics and create dashboards           |
| ELK               | Centralized log aggregation and searching         |
| OpenTelemetry     | Generate and export traces and telemetry          |
| Tempo             | Store distributed traces                          |

Spring Boot Admin is useful for:

- Quickly checking whether a service is UP or DOWN
- Viewing JVM details
- Changing log levels at runtime
- Viewing Spring beans and mappings
- Inspecting caches
- Viewing thread dumps

Prometheus and Grafana are better for:

- Historical metrics
- Alerting
- Capacity planning
- Trend analysis
- Service-level dashboards

Both can be used together.

---

## 26. Recommended PrimeCart Setup

For the PrimeCart project, use the following observability architecture:

```text
Microservices
   |
   |-- Spring Boot Actuator
   |
   |-- Spring Boot Admin --> Live application administration
   |
   |-- Prometheus ---------> Metrics storage
   |
   |-- Grafana ------------> Dashboards
   |
   |-- Logstash/ELK -------> Centralized logs
   |
   |-- OpenTelemetry ------> Distributed traces
   |
   `-- Tempo --------------> Trace storage
```

This setup demonstrates practical experience with:

- Application monitoring
- Metrics
- Logging
- Distributed tracing
- Health checks
- Runtime log-level management
- Production observability

---

## 27. Common Problems

### Application Is Not Showing in Admin Dashboard

Check:

```yaml
spring:
  boot:
    admin:
      client:
        url: http://localhost:9091
```

Verify that the Admin Server is running.

---

### Health Status Is Offline

Check whether the Actuator health endpoint is reachable:

```bash
curl http://localhost:8081/actuator/health
```

If authentication is enabled, verify the credentials configured in client metadata.

---

### Docker Connection Refused

Inside Docker, replace:

```text
localhost
```

with the Docker Compose service name.

Incorrect:

```yaml
url: http://localhost:9091
```

Correct:

```yaml
url: http://spring-boot-admin:9091
```

---

### Actuator Returns 401

Permit the required endpoint or configure HTTP Basic authentication.

```java
.requestMatchers("/actuator/health","/actuator/info").

permitAll()
```

---

### Logfile Tab Is Missing

Configure a log file:

```yaml
logging:
  file:
    name: logs/${spring.application.name}.log
```

Expose the logfile endpoint:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: logfile
```

---

## 28. Interview Explanation

A practical interview answer:

> I added Spring Boot Admin as a centralized operational dashboard for my PrimeCart microservices. Each service
> registers with the Admin Server and exposes secured Actuator endpoints. From the dashboard, I can check service
> health,
> JVM memory, threads, caches, mappings, metrics, and application logs. I can also change log levels at runtime for
> troubleshooting. I use Spring Boot Admin for live service administration, Prometheus and Grafana for historical
> metrics,
> ELK for centralized logs, and OpenTelemetry with Tempo for distributed tracing.

---

## 29. Final Checklist

- [ ] Admin Server project created
- [ ] `@EnableAdminServer` added
- [ ] Admin Server security configured
- [ ] Admin Client dependency added to every service
- [ ] Actuator dependency added
- [ ] Required Actuator endpoints exposed
- [ ] Actuator security configured
- [ ] Client registration URL configured
- [ ] Docker service names used inside containers
- [ ] Log file configured
- [ ] Application information exposed
- [ ] Admin dashboard accessible
- [ ] Every microservice shows `UP`
- [ ] Production credentials moved to environment variables