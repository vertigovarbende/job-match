package com.deveyk.jobmatch.identity.infrastructure.security;

import com.deveyk.jobmatch.identity.domain.Role;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String ROLE_AUTHORITY_PREFIX = "ROLE_";
    private static final String PREFERRED_USERNAME_CLAIM = "preferred_username";

    @Override
    public AbstractAuthenticationToken convert(final Jwt jwt) {
        final Collection<GrantedAuthority> authorities = extractRealmRoles(jwt);
        return new JwtAuthenticationToken(jwt, authorities, extractPrincipalName(jwt));
    }

    private Collection<GrantedAuthority> extractRealmRoles(final Jwt jwt) {
        return Role.parseKnown(RealmAccessRoleClaims.rawRoleNames(jwt)).stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(ROLE_AUTHORITY_PREFIX + role.name()))
                .collect(Collectors.toUnmodifiableSet());
    }

    private String extractPrincipalName(final Jwt jwt) {
        final String preferredUsername = jwt.getClaimAsString(PREFERRED_USERNAME_CLAIM);
        return preferredUsername != null ? preferredUsername : jwt.getSubject();
    }

}
