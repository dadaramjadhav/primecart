package com.primecart.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final KeycloakRoleConverter authoritiesConverter = new KeycloakRoleConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        return new JwtAuthenticationToken(jwt, authoritiesConverter.convert(jwt), jwt.getClaimAsString("preferred_username"));
    }
}