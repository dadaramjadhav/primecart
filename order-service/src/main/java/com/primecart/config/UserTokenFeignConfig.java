package com.primecart.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class UserTokenFeignConfig {

    @Bean
    public RequestInterceptor userTokenInterceptor() {

        return requestTemplate -> {

            var authentication = SecurityContextHolder
                    .getContext()
                    .getAuthentication();

            if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {

                throw new IllegalStateException("An authenticated user JWT is required");
            }

            String accessToken = jwtAuthentication
                    .getToken()
                    .getTokenValue();

            requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        };
    }
}