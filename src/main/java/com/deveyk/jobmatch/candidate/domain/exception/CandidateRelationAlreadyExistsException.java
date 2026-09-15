package com.deveyk.jobmatch.candidate.domain.exception;

import com.deveyk.jobmatch.candidate.domain.CandidateErrorCode;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchConflictException;

import java.io.Serial;

public class CandidateRelationAlreadyExistsException extends JobMatchConflictException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CandidateRelationAlreadyExistsException(final String relationType, final Long candidateId, final Long referenceId) {
        super(CandidateErrorCode.CANDIDATE_RELATION_ALREADY_EXISTS,
                relationType + " already exists for candidateId=" + candidateId + ", referenceId=" + referenceId);
    }

    public CandidateRelationAlreadyExistsException(final String relationType, final Long candidateId, final Long referenceId, final Throwable cause) {
        super(CandidateErrorCode.CANDIDATE_RELATION_ALREADY_EXISTS, relationType + " already exists for candidateId=" + candidateId + ", referenceId=" + referenceId, cause);
    }

}