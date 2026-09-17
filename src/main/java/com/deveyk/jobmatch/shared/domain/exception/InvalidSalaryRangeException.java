package com.deveyk.jobmatch.shared.domain.exception;

import com.deveyk.jobmatch.shared.domain.SharedErrorCode;

import java.io.Serial;

public class InvalidSalaryRangeException extends DomainRuleViolationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidSalaryRangeException(final String message) {
        super(SharedErrorCode.INVALID_SALARY_RANGE, message);
    }

}
