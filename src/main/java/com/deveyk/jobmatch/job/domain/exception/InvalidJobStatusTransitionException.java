package com.deveyk.jobmatch.job.domain.exception;

import com.deveyk.jobmatch.job.domain.JobErrorCode;
import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import com.deveyk.jobmatch.shared.domain.exception.DomainRuleViolationException;

import java.io.Serial;

public class InvalidJobStatusTransitionException extends DomainRuleViolationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidJobStatusTransitionException(final JobStatusType from, final String action) {
        super(JobErrorCode.INVALID_JOB_STATUS_TRANSITION, "Cannot " + action + " a job in " + from + " status");
    }

}
