package com.deveyk.jobmatch.candidate.domain.exception;

import com.deveyk.jobmatch.candidate.domain.CandidateErrorCode;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchResourceNotFoundException;

import java.io.Serial;

public class CandidateNotFoundException extends JobMatchResourceNotFoundException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CandidateNotFoundException(final Long userId) {
        super(CandidateErrorCode.CANDIDATE_NOT_FOUND, "No candidate profile found for userId=" + userId);
    }

}