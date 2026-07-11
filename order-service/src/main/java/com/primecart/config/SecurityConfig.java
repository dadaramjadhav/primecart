package com.primecart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/orders/**")
                        .hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/orders/**")
                        .hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/orders/status/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/orders/**")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth ->
                        oauth.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(new JwtAuthenticationConverter())
                        )
                );

        return http.build();
    }
}