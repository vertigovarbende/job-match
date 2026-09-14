package com.deveyk.jobmatch.identity.domain.exception;

import com.deveyk.jobmatch.shared.domain.exception.DomainRuleViolationException;

import java.io.Serial;

public class InvalidBusinessRoleClaimException extends DomainRuleViolationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidBusinessRoleClaimException(final int foundRoleCount) {
        super(IdentityErrorCode.INVALID_BUSINESS_ROLE_CLAIM, "Expected exactly one business role in JWT, found " + foundRoleCount);
    }

}
