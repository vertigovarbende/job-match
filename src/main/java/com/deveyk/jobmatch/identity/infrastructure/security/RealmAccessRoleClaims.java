package com.deveyk.jobmatch.identity.infrastructure.security;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


final class RealmAccessRoleClaims {

    private static final String REALM_ACCESS_CLAIM = "realm_access";
    private static final String ROLES_KEY = "roles";

    private RealmAccessRoleClaims() {
    }

    static Collection<String> rawRoleNames(final Jwt jwt) {
        final Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS_CLAIM);
        if (realmAccess == null) {
            return Set.of();
        }

        final Object roles = realmAccess.get(ROLES_KEY);
        return roles == null ? Set.of() : castRoles(roles);
    }

    @SuppressWarnings("unchecked")
    private static Collection<String> castRoles(final Object rolesClaim) {
        return (Collection<String>) rolesClaim;
    }

}
