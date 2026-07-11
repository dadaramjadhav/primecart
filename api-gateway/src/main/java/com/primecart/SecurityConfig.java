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
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/actuator/**"
                        ).permitAll()

                        // Product Service
                        .pathMatchers(HttpMethod.GET, "/api/products/**")
                        .hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.POST, "/api/products/**")
                        .hasRole("ADMIN")

                        .pathMatchers(HttpMethod.PUT, "/api/products/**")
                        .hasRole("ADMIN")

                        .pathMatchers(HttpMethod.DELETE, "/api/products/**")
                        .hasRole("ADMIN")

                        // Order Service
                        .pathMatchers(HttpMethod.POST, "/api/orders/**")
                        .hasRole("USER")

                        .pathMatchers(HttpMethod.GET, "/api/orders/**")
                        .hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.PUT, "/api/orders/**")
                        .hasRole("ADMIN")

                        .pathMatchers(HttpMethod.DELETE, "/api/orders/**")
                        .hasRole("ADMIN")

                        // Everything else requires authentication
                        .anyExchange()
                        .authenticated()
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