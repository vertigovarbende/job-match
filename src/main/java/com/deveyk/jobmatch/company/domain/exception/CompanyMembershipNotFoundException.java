package com.deveyk.jobmatch.company.domain.exception;

import com.deveyk.jobmatch.company.domain.CompanyErrorCode;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchResourceNotFoundException;

import java.io.Serial;

public class CompanyMembershipNotFoundException extends JobMatchResourceNotFoundException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CompanyMembershipNotFoundException(final Long companyId, final Long userId) {
        super(CompanyErrorCode.COMPANY_MEMBERSHIP_NOT_FOUND, "No membership found for companyId=" + companyId + ", userId=" + userId);
    }

}
