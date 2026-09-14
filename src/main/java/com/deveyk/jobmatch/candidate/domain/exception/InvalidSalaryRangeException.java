package com.deveyk.jobmatch.candidate.domain.exception;

import com.deveyk.jobmatch.candidate.domain.CandidateErrorCode;
import com.deveyk.jobmatch.shared.domain.exception.DomainRuleViolationException;

import java.io.Serial;

public class InvalidSalaryRangeException extends DomainRuleViolationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidSalaryRangeException(final String message) {
        super(CandidateErrorCode.INVALID_SALARY_RANGE, message);
    }

}