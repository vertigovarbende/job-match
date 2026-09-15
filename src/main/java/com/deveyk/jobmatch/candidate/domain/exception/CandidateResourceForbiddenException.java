package com.deveyk.jobmatch.candidate.domain.exception;

import com.deveyk.jobmatch.candidate.domain.CandidateErrorCode;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchForbiddenException;

import java.io.Serial;

public class CandidateResourceForbiddenException extends JobMatchForbiddenException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CandidateResourceForbiddenException(final String resourceType, final Long resourceId, final Long candidateId) {
        super(CandidateErrorCode.CANDIDATE_RESOURCE_FORBIDDEN,
                resourceType + " id=" + resourceId + " does not belong to candidateId=" + candidateId);
    }

    public CandidateResourceForbiddenException(final String resourceType, final String resourceId, final Long candidateId) {
        super(CandidateErrorCode.CANDIDATE_RESOURCE_FORBIDDEN, resourceType + " id=" + resourceId + " does not belong to candidateId=" + candidateId);
    }

}