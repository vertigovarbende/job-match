package com.deveyk.jobmatch.job.domain.exception;

import com.deveyk.jobmatch.job.domain.JobErrorCode;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchForbiddenException;

import java.io.Serial;

public class CompanyMembershipRequiredException extends JobMatchForbiddenException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CompanyMembershipRequiredException() {
        super(JobErrorCode.COMPANY_MEMBERSHIP_REQUIRED, "You must belong to a company before creating a job");
    }

}
