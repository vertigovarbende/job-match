package com.deveyk.jobmatch.candidate.domain.exception;

import com.deveyk.jobmatch.candidate.domain.CandidateErrorCode;
import com.deveyk.jobmatch.identity.domain.Role;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchForbiddenException;

import java.io.Serial;

public class CandidateRoleRequiredException extends JobMatchForbiddenException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CandidateRoleRequiredException(final Role actualRole) {
        super(CandidateErrorCode.CANDIDATE_ROLE_REQUIRED, "Only CANDIDATE role can own a candidate profile, but was " + actualRole);
    }

}