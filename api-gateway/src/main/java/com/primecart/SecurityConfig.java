package com.primecart;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .authorizeExchange(exchange -> exchange

                        // Public endpoints
                        .pathMatchers(
                                "/auth/token",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/actuator/**"
                        ).permitAll()
                        .pathMatchers("/dev/token").permitAll()

                        .pathMatchers("/debug").permitAll()

                        // Product Service
                        .pathMatchers(HttpMethod.GET, "/api/products/**").hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")

                        .pathMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")

                        .pathMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

                        // Order Service
                        .pathMatchers(HttpMethod.POST, "/api/orders/**").hasRole("USER")

                        .pathMatchers(HttpMethod.GET, "/api/orders/**").hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.PUT, "/api/orders/**").hasRole("ADMIN")

                        .pathMatchers(HttpMethod.DELETE, "/api/orders/**").hasRole("ADMIN")

                        // Cart Service
                        .pathMatchers(HttpMethod.GET, "/api/cart/**").hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.POST, "/api/cart/**").hasRole("USER")

                        .pathMatchers(HttpMethod.PUT, "/api/cart/**").hasRole("USER")

                        .pathMatchers(HttpMethod.DELETE, "/api/cart/**").hasRole("USER")

                        // Inventory Service APIs
                        .pathMatchers(HttpMethod.GET, "/api/inventory/**").hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.POST, "/api/inventory").hasRole("ADMIN")

                        .pathMatchers(HttpMethod.POST, "/api/inventory/**").hasRole("ADMIN")

                        .pathMatchers(HttpMethod.PUT, "/api/inventory/**").hasRole("ADMIN")

                        .pathMatchers(HttpMethod.DELETE, "/api/inventory/**").hasRole("ADMIN")

                        .pathMatchers(HttpMethod.GET, "/api/payments/**").hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.POST, "/api/payments/**").hasRole("USER")

                        .pathMatchers(HttpMethod.PUT, "/api/payments/**").hasRole("ADMIN")

                        .pathMatchers(HttpMethod.DELETE, "/api/payments/**").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )

                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(
                                new ReactiveJwtAuthenticationConverterAdapter(
                                        new JwtAuthenticationConverter()
                                )
                        ))
                )

                .build();
    }
}