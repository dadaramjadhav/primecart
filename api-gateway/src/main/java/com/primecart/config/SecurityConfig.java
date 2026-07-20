package com.primecart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveJwtDecoder jwtDecoder) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .cors(Customizer.withDefaults())

                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()
                        // Public endpoints
                        .pathMatchers("/auth/token", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
                        .permitAll()
                        .pathMatchers("/actuator/health/**", "/actuator/info", "/actuator/prometheus")
                        .permitAll()

                        .pathMatchers("/actuator", "/actuator/metrics/**", "/actuator/caches/**")
                        .hasRole("ACTUATOR_ADMIN")

                        .pathMatchers("/dev/token")
                        .permitAll()

                        // Product Service
                        .pathMatchers(HttpMethod.GET, "/api/products/**")
                        .hasRole("PRODUCT_READ")

                        .pathMatchers(HttpMethod.POST, "/api/products/**")
                        .hasRole("PRODUCT_CREATE")

                        .pathMatchers(HttpMethod.PUT, "/api/products/**")
                        .hasRole("PRODUCT_UPDATE")

                        .pathMatchers(HttpMethod.DELETE, "/api/products/**")
                        .hasRole("PRODUCT_DELETE")

                        .pathMatchers(HttpMethod.GET, "/api/categories/**")
                        .hasRole("CATEGORY_READ")

                        .pathMatchers(HttpMethod.GET, "/api/brands/**")
                        .hasRole("BRAND_READ")

                        // Order Service
                        // Block internal service-to-service endpoints at the gateway.
                        // Payment-service calls order-service directly on port 8082.
                        .pathMatchers(HttpMethod.PUT, "/api/orders/*/payment-success", "/api/orders/*/payment-failed",
                                      "/api/orders/*/retry-payment", "/api/orders/*/confirm")
                        .denyAll()

                        .pathMatchers(HttpMethod.GET, "/api/orders/customer/**", "/api/orders/status/**")
                        .hasRole("ORDER_READ_ALL")

                        .pathMatchers(HttpMethod.POST, "/api/orders")
                        .hasRole("ORDER_CREATE")

                        .pathMatchers(HttpMethod.GET, "/api/orders", "/api/orders/*", "/api/orders/order-number/**")
                        .hasAnyRole("ORDER_READ_OWN", "ORDER_READ_ALL")

                        .pathMatchers(HttpMethod.PUT, "/api/orders/*/cancel")
                        .hasRole("ORDER_CANCEL")

                        .pathMatchers(HttpMethod.DELETE, "/api/orders/*")
                        .hasRole("ORDER_DELETE")

                        // Cart Service
                        .pathMatchers(HttpMethod.GET, "/api/cart")
                        .hasRole("CART_READ")

                        .pathMatchers(HttpMethod.POST, "/api/cart/items")
                        .hasRole("CART_ITEM_ADD")

                        .pathMatchers(HttpMethod.PUT, "/api/cart/items/**")
                        .hasRole("CART_ITEM_UPDATE")

                        .pathMatchers(HttpMethod.DELETE, "/api/cart")
                        .hasRole("CART_CLEAR")

                        .pathMatchers(HttpMethod.DELETE, "/api/cart/items/**")
                        .hasRole("CART_ITEM_REMOVE")

                        // Inventory Service APIs
                        // Block internal service-to-service endpoints at the gateway.
                        // Order-service calls inventory-service directly on port 8084.
                        .pathMatchers(HttpMethod.POST, "/api/inventory/reserve", "/api/inventory/release", "/api/inventory/confirm")
                        .denyAll()
                        .pathMatchers(HttpMethod.GET, "/api/inventory/**")
                        .hasRole("INVENTORY_READ")

                        .pathMatchers(HttpMethod.POST, "/api/inventory")
                        .hasRole("INVENTORY_CREATE")

                        .pathMatchers(HttpMethod.PUT, "/api/inventory/*/increase")
                        .hasRole("INVENTORY_INCREASE")

                        .pathMatchers(HttpMethod.PUT, "/api/inventory/*/decrease")
                        .hasRole("INVENTORY_DECREASE")

                        // Payment service
                        .pathMatchers(HttpMethod.GET, "/api/payments/**")
                        .hasRole("PAYMENT_READ")

                        .pathMatchers(HttpMethod.POST, "/api/payments")
                        .hasRole("PAYMENT_CREATE")

                        .pathMatchers(HttpMethod.PUT, "/api/payments/*/success")
                        .hasRole("PAYMENT_SUCCESS")

                        .pathMatchers(HttpMethod.PUT, "/api/payments/*/failed")
                        .hasRole("PAYMENT_FAIL")

                        .pathMatchers(HttpMethod.PUT, "/api/payments/*/retry")
                        .hasRole("PAYMENT_RETRY")

                        .pathMatchers(HttpMethod.GET, "/api/customers/me")
                        .hasRole("PROFILE_READ")

                        .pathMatchers(HttpMethod.PUT, "/api/customers/me")
                        .hasRole("PROFILE_UPDATE")
                        
                        .anyExchange()
                        .authenticated())

                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt
                        .jwtDecoder(jwtDecoder)
                        .jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(new JwtAuthenticationConverter()))))

                .build();
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
            String issuerUri,
            @Value("${primecart.security.jwt.audience}")
            String expectedAudience) {

        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder
                .withIssuerLocation(issuerUri)
                .build();

        OAuth2TokenValidator<Jwt> issuerValidator = JwtValidators.createDefaultWithIssuer(issuerUri);

        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(expectedAudience);

        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(issuerValidator, audienceValidator);

        decoder.setJwtValidator(validator);

        return decoder;
    }
}
