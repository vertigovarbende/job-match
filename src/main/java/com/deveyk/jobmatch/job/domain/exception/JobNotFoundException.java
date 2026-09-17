package com.deveyk.jobmatch.job.domain.exception;

import com.deveyk.jobmatch.job.domain.JobErrorCode;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchResourceNotFoundException;

import java.io.Serial;

public class JobNotFoundException extends JobMatchResourceNotFoundException {

    @Serial
    private static final long serialVersionUID = 1L;

    public JobNotFoundException(final Long id) {
        super(JobErrorCode.JOB_NOT_FOUND, "No job found for id=" + id);
    }

}
