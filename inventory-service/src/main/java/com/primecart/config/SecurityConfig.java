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
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());

        return http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                        .permitAll()

                        .requestMatchers("/actuator/health/**", "/actuator/info", "/actuator/prometheus")
                        .permitAll()

                        .requestMatchers("/actuator", "/actuator/metrics/**", "/actuator/caches/**")
                        .hasRole("ACTUATOR_ADMIN")
                        
                        .requestMatchers(HttpMethod.GET, "/api/inventory/**")
                        .hasRole("INVENTORY_READ")

                        .requestMatchers(HttpMethod.POST, "/api/inventory")
                        .hasRole("INVENTORY_CREATE")

                        // Order-service calls made on behalf of an authenticated
                        // customer must be matched before the admin-only wildcard.
                        .requestMatchers(HttpMethod.PUT, "/api/inventory/*/increase")
                        .hasRole("INVENTORY_INCREASE")

                        .requestMatchers(HttpMethod.PUT, "/api/inventory/*/decrease")
                        .hasRole("INVENTORY_DECREASE")

                        .requestMatchers(HttpMethod.POST, "/api/inventory/reserve")
                        .hasRole("INVENTORY_RESERVE")

                        .requestMatchers(HttpMethod.POST, "/api/inventory/release")
                        .hasRole("INVENTORY_RELEASE")

                        .requestMatchers(HttpMethod.POST, "/api/inventory/confirm")
                        .hasRole("INVENTORY_CONFIRM")

//                        .requestMatchers(HttpMethod.POST, "/api/inventory/**")
//                        .hasRole("ADMIN")
//
//                        .requestMatchers(HttpMethod.PUT, "/api/inventory/**")
//                        .hasRole("ADMIN")
//
//                        .requestMatchers(HttpMethod.DELETE, "/api/inventory/**")
//                        .hasRole("ADMIN")
                        .anyRequest()
                        .authenticated())

                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt
                        .decoder(jwtDecoder)
                        .jwtAuthenticationConverter(converter)))

                .build();
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
