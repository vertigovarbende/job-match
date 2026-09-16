package com.deveyk.jobmatch.company.domain.exception;

import com.deveyk.jobmatch.company.domain.CompanyErrorCode;
import com.deveyk.jobmatch.identity.domain.Role;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchForbiddenException;

import java.io.Serial;

public class CompanyMembershipRoleRequiredException extends JobMatchForbiddenException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CompanyMembershipRoleRequiredException(final Role actualRole) {
        super(CompanyErrorCode.COMPANY_ROLE_REQUIRED, "Only EMPLOYER role can join a company, but was " + actualRole);
    }

}
