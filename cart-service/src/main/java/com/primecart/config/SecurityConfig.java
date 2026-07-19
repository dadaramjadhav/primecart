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

                        .requestMatchers("/actuator", "/actuator/metrics/**", "/actuator/caches/**")
                        .hasRole("ACTUATOR_ADMIN")
                        
                        .requestMatchers(HttpMethod.GET, "/api/cart/**")
                        .hasRole("CART_READ")

                        .requestMatchers(HttpMethod.POST, "/api/cart/**")
                        .hasRole("CART_ITEM_ADD")

                        .requestMatchers(HttpMethod.PUT, "/api/cart/**")
                        .hasRole("CART_ITEM_UPDATE")

                        .requestMatchers(HttpMethod.DELETE, "/api/cart/**")
                        .hasRole("CART_ITEM_REMOVE")

                        .requestMatchers(HttpMethod.DELETE, "/api/cart")
                        .hasRole("CART_CLEAR")

                        .anyRequest()
                        .authenticated())
/*This configuration enables JWT-based authentication for the Resource Server and tells Spring Security
to use the custom JwtAuthenticationConverter to convert incoming JWTs into authenticated users with the correct roles and authorities.*/.oauth2ResourceServer(
                        oauth -> oauth.jwt(jwt -> jwt
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