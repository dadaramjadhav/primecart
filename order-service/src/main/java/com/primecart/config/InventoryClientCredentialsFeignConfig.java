package com.primecart.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

public class InventoryClientCredentialsFeignConfig {

    private static final String REGISTRATION_ID = "order-service-client";

    private static final String PRINCIPAL_NAME = "primecart-order-service";

    @Bean
    public RequestInterceptor inventoryAccessTokenInterceptor(OAuth2AuthorizedClientManager authorizedClientManager) {

        return requestTemplate -> {

            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                    .withClientRegistrationId(REGISTRATION_ID)
                    .principal(PRINCIPAL_NAME)
                    .build();

            OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

            if (authorizedClient == null) {
                throw new IllegalStateException("Unable to obtain order-service access token");
            }

            String accessToken = authorizedClient
                    .getAccessToken()
                    .getTokenValue();

            requestTemplate.header("Authorization", "Bearer " + accessToken);
        };
    }
}