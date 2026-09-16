package com.deveyk.jobmatch.company.domain.exception;

import com.deveyk.jobmatch.company.domain.CompanyErrorCode;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchResourceNotFoundException;

import java.io.Serial;

public class CompanyNotFoundException extends JobMatchResourceNotFoundException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CompanyNotFoundException(final Long id) {
        super(CompanyErrorCode.COMPANY_NOT_FOUND, "No company found for id=" + id);
    }

}
