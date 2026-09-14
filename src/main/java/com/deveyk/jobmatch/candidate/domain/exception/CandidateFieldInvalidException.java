package com.deveyk.jobmatch.candidate.domain.exception;

import com.deveyk.jobmatch.candidate.domain.CandidateErrorCode;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchInvalidArgumentException;

import java.io.Serial;

public class CandidateFieldInvalidException extends JobMatchInvalidArgumentException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CandidateFieldInvalidException(final String fieldName) {
        super(CandidateErrorCode.CANDIDATE_FIELD_INVALID, fieldName + " must not be blank");
    }

    public CandidateFieldInvalidException(final String fieldName, final String reason) {
        super(CandidateErrorCode.CANDIDATE_FIELD_INVALID, fieldName + " " + reason);
    }

}