package com.deveyk.jobmatch.identity.application;

import com.deveyk.jobmatch.identity.domain.exception.InvalidBusinessRoleClaimException;
import com.deveyk.jobmatch.identity.domain.model.JmUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserFacade {

    private final CurrentUserResolver currentUserResolver;

    public JmUser resolveCurrentUser() {

        final Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();

        if (details instanceof InvalidBusinessRoleClaimException exception) {
            throw exception;
        }

        return this.currentUserResolver.resolve((AuthenticatedUserClaims) details);
    }

}
