package com.primecart.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*This class extracts user roles from the realm_access.roles claim in the Keycloak JWT.
It converts each role into a Spring Security GrantedAuthority (prefixed with ROLE_)
so that role-based authorization (e.g., hasRole("ADMIN")) works correctly.*/
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        Map<String, Object> realmAccess = jwt.getClaim(SecurityConstants.REALM_ACCESS);

        if (realmAccess == null) {
            return Collections.emptyList();
        }

        List<String> roles = (List<String>) realmAccess.get(SecurityConstants.ROLES);

        if (roles == null) {
            return Collections.emptyList();
        }

        return roles
                .stream()
                .map(role -> new SimpleGrantedAuthority(SecurityConstants.ROLE_PREFIX + role))
                .collect(Collectors.toSet());
    }
}