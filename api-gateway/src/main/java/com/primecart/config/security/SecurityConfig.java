package com.primecart.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .cors(Customizer.withDefaults())

                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()
                        // Public endpoints
                        .pathMatchers("/auth/token", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/actuator/**")
                        .permitAll()
                        .pathMatchers("/dev/token")
                        .permitAll()

                        // Product Service
                        .pathMatchers(HttpMethod.GET, "/api/products/**")
                        .hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.POST, "/api/products/**")
                        .hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.PUT, "/api/products/**")
                        .hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.DELETE, "/api/products/**")
                        .hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.GET, "/api/categories/**", "/api/brands/**")
                        .hasAnyRole("USER", "ADMIN")
                        // Order Service
                        .pathMatchers(HttpMethod.POST, "/api/orders/**")
                        .hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.GET, "/api/orders/**")
                        .hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.PUT, "/api/orders/**")
                        .hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.DELETE, "/api/orders/**")
                        .hasAnyRole("USER", "ADMIN")

                        // Cart Service
                        .pathMatchers(HttpMethod.GET, "/api/cart/**")
                        .hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.POST, "/api/cart/**")
                        .hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.PUT, "/api/cart/**")
                        .hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.DELETE, "/api/cart/**")
                        .hasAnyRole("USER", "ADMIN")

                        // Inventory Service APIs
                        .pathMatchers(HttpMethod.GET, "/api/inventory/**")
                        .hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.POST, "/api/inventory")
                        .hasRole("ADMIN")

                        .pathMatchers(HttpMethod.POST, "/api/inventory/**")
                        .hasRole("ADMIN")

                        .pathMatchers(HttpMethod.PUT, "/api/inventory/**")
                        .hasRole("ADMIN")

                        .pathMatchers(HttpMethod.DELETE, "/api/inventory/**")
                        .hasRole("ADMIN")

                        .pathMatchers(HttpMethod.GET, "/api/payments/**")
                        .hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.POST, "/api/payments/**")
                        .hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.PUT, "/api/payments/**")
                        .hasAnyRole("ADMIN", "USER")

                        .pathMatchers(HttpMethod.DELETE, "/api/payments/**")
                        .hasRole("ADMIN")
                        .anyExchange()
                        .authenticated())

                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(
                        new ReactiveJwtAuthenticationConverterAdapter(new JwtAuthenticationConverter()))))

                .build();
    }
}