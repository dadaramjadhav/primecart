package com.primecart.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class KeycloakJwtAuthenticationConverter
        implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        Map<String, Object> realmAccess =
                jwt.getClaim(SecurityConstants.REALM_ACCESS);

        if (realmAccess == null) {
            return List.of();
        }

        List<String> roles =
                (List<String>) realmAccess.get(SecurityConstants.ROLES);

        if (roles == null) {
            return List.of();
        }

        return roles.stream()
                    .map(role -> new SimpleGrantedAuthority(
                            SecurityConstants.ROLE_PREFIX + role
                    ))
                    .collect(Collectors.toList());
    }
}
