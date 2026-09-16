package com.deveyk.jobmatch.company.domain.exception;

import com.deveyk.jobmatch.company.domain.CompanyErrorCode;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchConflictException;

import java.io.Serial;

public class CompanyMembershipAlreadyExistsException extends JobMatchConflictException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CompanyMembershipAlreadyExistsException(final Long userId) {
        super(CompanyErrorCode.COMPANY_MEMBERSHIP_ALREADY_EXISTS, "Employer already belongs to a company: userId=" + userId);
    }

    public CompanyMembershipAlreadyExistsException(final Long userId, final Throwable cause) {
        super(CompanyErrorCode.COMPANY_MEMBERSHIP_ALREADY_EXISTS, "Employer already belongs to a company: userId=" + userId, cause);
    }

}
