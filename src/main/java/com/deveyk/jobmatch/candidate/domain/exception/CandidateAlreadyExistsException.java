package com.deveyk.jobmatch.candidate.domain.exception;

import com.deveyk.jobmatch.candidate.domain.CandidateErrorCode;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchConflictException;

import java.io.Serial;

public class CandidateAlreadyExistsException extends JobMatchConflictException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CandidateAlreadyExistsException(final Long userId) {
        super(CandidateErrorCode.CANDIDATE_ALREADY_EXISTS, "A candidate profile already exists for userId=" + userId);
    }

    public CandidateAlreadyExistsException(final Long userId, final Throwable cause) {
        super(CandidateErrorCode.CANDIDATE_ALREADY_EXISTS, "A candidate profile already exists for userId=" + userId, cause);
    }

}