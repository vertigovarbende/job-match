package com.deveyk.jobmatch.company.domain.exception;

import com.deveyk.jobmatch.company.domain.CompanyErrorCode;
import com.deveyk.jobmatch.identity.domain.Role;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchForbiddenException;

import java.io.Serial;

public class CompanyRoleRequiredException extends JobMatchForbiddenException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CompanyRoleRequiredException(final Role actualRole) {
        super(CompanyErrorCode.COMPANY_ROLE_REQUIRED, "Only EMPLOYER role can create a company, but was " + actualRole);
    }

}
