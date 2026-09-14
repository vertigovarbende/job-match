package com.deveyk.jobmatch.identity.infrastructure.security;

import com.deveyk.jobmatch.identity.application.AuthenticatedUserClaims;
import com.deveyk.jobmatch.identity.domain.Role;
import com.deveyk.jobmatch.identity.domain.exception.InvalidBusinessRoleClaimException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class JwtClaimsExtractor {

    public AuthenticatedUserClaims extract(final Jwt jwt) {
        return AuthenticatedUserClaims.builder()
                .subjectId(UUID.fromString(jwt.getSubject()))
                .email(jwt.getClaimAsString("email"))
                .role(extractSingleBusinessRole(jwt))
                .build();
    }

    private Role extractSingleBusinessRole(final Jwt jwt) {
        final Set<Role> businessRoles = Role.parseKnown(RealmAccessRoleClaims.rawRoleNames(jwt));

        if (businessRoles.size() != 1) {
            throw new InvalidBusinessRoleClaimException(businessRoles.size());
        }

        return businessRoles.iterator().next();
    }

}
