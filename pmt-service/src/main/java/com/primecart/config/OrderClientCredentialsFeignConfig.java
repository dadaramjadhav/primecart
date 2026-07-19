package com.primecart.config;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Slf4j
public class OrderClientCredentialsFeignConfig {

    private static final String REGISTRATION_ID = "payment-service-client";

    private static final String PRINCIPAL_NAME = "primecart-payment-service";

    @Bean
    public RequestInterceptor orderServiceTokenInterceptor(OAuth2AuthorizedClientManager authorizedClientManager) {

        return requestTemplate -> {

            // Token received from the UI
            var authentication = SecurityContextHolder
                    .getContext()
                    .getAuthentication();

            // Generate payment-service client-credentials token
            OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest
                    .withClientRegistrationId(REGISTRATION_ID)
                    .principal(PRINCIPAL_NAME)
                    .build();

            OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(request);

            if (authorizedClient == null) {
                throw new IllegalStateException("Unable to obtain payment-service access token");
            }

            String serviceAccessToken = authorizedClient
                    .getAccessToken()
                    .getTokenValue();
            //start********* just for adding token in console for checking
            if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {

                String uiAccessToken = jwtAuthentication
                        .getToken()
                        .getTokenValue();

                log.trace("INCOMING UI ACCESS TOKEN: {}", uiAccessToken);

                log.trace("Incoming UI token subject={}, username={}, audience={}", jwtAuthentication
                        .getToken()
                        .getSubject(), jwtAuthentication
                                  .getToken()
                                  .getClaimAsString("preferred_username"), jwtAuthentication
                                  .getToken()
                                  .getAudience());
            } else {
                log.trace("No incoming UI JWT found in SecurityContext");
            }

            log.trace("OUTGOING PAYMENT-SERVICE TOKEN: {}", serviceAccessToken);
            //end****************

            requestTemplate.header("Authorization", "Bearer " + serviceAccessToken);
        };
    }
}