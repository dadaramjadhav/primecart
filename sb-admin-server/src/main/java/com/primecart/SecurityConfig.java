package com.primecart;

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