package com.deveyk.jobmatch.company.domain.exception;

import com.deveyk.jobmatch.company.domain.CompanyErrorCode;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchInvalidArgumentException;

import java.io.Serial;

public class CompanyFieldInvalidException extends JobMatchInvalidArgumentException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CompanyFieldInvalidException(final String fieldName) {
        super(CompanyErrorCode.COMPANY_FIELD_INVALID, fieldName + " must not be blank");
    }

    public CompanyFieldInvalidException(final String fieldName, final String reason) {
        super(CompanyErrorCode.COMPANY_FIELD_INVALID, fieldName + " " + reason);
    }

}
