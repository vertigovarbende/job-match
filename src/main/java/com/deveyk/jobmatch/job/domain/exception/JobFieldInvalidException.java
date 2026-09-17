package com.deveyk.jobmatch.job.domain.exception;

import com.deveyk.jobmatch.job.domain.JobErrorCode;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchInvalidArgumentException;

import java.io.Serial;

public class JobFieldInvalidException extends JobMatchInvalidArgumentException {

    @Serial
    private static final long serialVersionUID = 1L;

    public JobFieldInvalidException(final String fieldName) {
        super(JobErrorCode.JOB_FIELD_INVALID, fieldName + " must not be blank");
    }

    public JobFieldInvalidException(final String fieldName, final String reason) {
        super(JobErrorCode.JOB_FIELD_INVALID, fieldName + " " + reason);
    }

}
