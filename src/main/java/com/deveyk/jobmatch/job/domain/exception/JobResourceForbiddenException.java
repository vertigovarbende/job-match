package com.deveyk.jobmatch.job.domain.exception;

import com.deveyk.jobmatch.job.domain.JobErrorCode;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchForbiddenException;

import java.io.Serial;

public class JobResourceForbiddenException extends JobMatchForbiddenException {

    @Serial
    private static final long serialVersionUID = 1L;

    public JobResourceForbiddenException(final Long jobId, final Long companyId) {
        super(JobErrorCode.JOB_RESOURCE_FORBIDDEN, "Job id=" + jobId + " does not belong to companyId=" + companyId);
    }

}
