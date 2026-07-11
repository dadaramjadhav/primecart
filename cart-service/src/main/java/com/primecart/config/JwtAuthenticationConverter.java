package com.primecart.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/*This class converts a JWT received from Keycloak into a Spring Security JwtAuthenticationToken.
It extracts the user's roles using KeycloakRoleConverter and sets the authenticated user's username (preferred_username)
as the principal, enabling Spring Security to perform authentication and authorization.*/
public class JwtAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    private final KeycloakRoleConverter authoritiesConverter =
            new KeycloakRoleConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        return new JwtAuthenticationToken(
                jwt,
                authoritiesConverter.convert(jwt),
                jwt.getClaimAsString("preferred_username")
        );
    }
}