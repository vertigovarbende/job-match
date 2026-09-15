package com.deveyk.jobmatch.candidate.domain.exception;

import com.deveyk.jobmatch.candidate.domain.CandidateErrorCode;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchResourceNotFoundException;

import java.io.Serial;

public class CandidateResourceNotFoundException extends JobMatchResourceNotFoundException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CandidateResourceNotFoundException(final String resourceType, final Long id) {
        super(CandidateErrorCode.CANDIDATE_RESOURCE_NOT_FOUND, resourceType + " not found: id=" + id);
    }

    public CandidateResourceNotFoundException(final String resourceType, final String id) {
        super(CandidateErrorCode.CANDIDATE_RESOURCE_NOT_FOUND, resourceType + " not found: id=" + id);
    }

}