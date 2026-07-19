package com.primecart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                        .permitAll()

                        .requestMatchers("/actuator/health/**", "/actuator/info", "/actuator/prometheus")
                        .permitAll()

                        .requestMatchers("/actuator", "/actuator/metrics/**", "/actuator/caches/**", "/actuator/circuitbreakers/**",
                                         "/actuator/circuitbreakerevents/**")
                        .hasRole("ACTUATOR_ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/orders/*/payment-success")
                        .hasRole("ORDER_PAYMENT_SUCCESS")

                        .requestMatchers(HttpMethod.PUT, "/api/orders/*/payment-failed")
                        .hasRole("ORDER_PAYMENT_FAIL")

                        // Administrative queries
                        .requestMatchers(HttpMethod.GET, "/api/orders/customer/**", "/api/orders/status/**")
                        .hasRole("ORDER_READ_ALL")

                        // Customer order creation
                        .requestMatchers(HttpMethod.POST, "/api/orders")
                        .hasRole("ORDER_CREATE")

                        // Customer and administrative reads
                        .requestMatchers(HttpMethod.GET, "/api/orders", "/api/orders/*", "/api/orders/order-number/**")
                        .hasAnyRole("ORDER_READ_OWN", "ORDER_READ_ALL")

                        .requestMatchers(HttpMethod.PUT, "/api/orders/*/cancel")
                        .hasRole("ORDER_CANCEL")

                        .requestMatchers(HttpMethod.DELETE, "/api/orders/*")
                        .hasRole("ORDER_DELETE")

                        .requestMatchers(HttpMethod.PUT, "/api/orders/*/confirm")
                        .hasRole("ORDER_CONFIRM")

                        .requestMatchers(HttpMethod.PUT, "/api/orders/*/retry-payment")
                        .hasRole("ORDER_PAYMENT_RETRY")

                        .anyRequest()
                        .authenticated())

                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt
                        .decoder(jwtDecoder)
                        .jwtAuthenticationConverter(new JwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder(
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
            String issuerUri,
            @Value("${primecart.security.jwt.audience}")
            String expectedAudience) {

        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withIssuerLocation(issuerUri)
                .build();

        OAuth2TokenValidator<Jwt> issuerValidator = JwtValidators.createDefaultWithIssuer(issuerUri);

        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(expectedAudience);

        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(issuerValidator, audienceValidator);

        decoder.setJwtValidator(validator);

        return decoder;
    }
}
