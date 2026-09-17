package com.deveyk.jobmatch.job.domain.exception;

import com.deveyk.jobmatch.job.domain.JobErrorCode;
import com.deveyk.jobmatch.shared.domain.exception.DomainRuleViolationException;

import java.io.Serial;

public class JobNotReadyForPublishException extends DomainRuleViolationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public JobNotReadyForPublishException() {
        super(JobErrorCode.JOB_NOT_READY_FOR_PUBLISH, "Job is missing required fields (location, salaryRange, employmentType, workplaceType) and cannot be published");
    }

}
