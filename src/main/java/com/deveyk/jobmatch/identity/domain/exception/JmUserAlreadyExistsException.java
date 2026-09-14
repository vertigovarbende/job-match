package com.deveyk.jobmatch.identity.domain.exception;

import com.deveyk.jobmatch.shared.domain.exception.JobMatchConflictException;

import java.io.Serial;
import java.util.UUID;

public class JmUserAlreadyExistsException extends JobMatchConflictException {

    @Serial
    private static final long serialVersionUID = 1L;

    public JmUserAlreadyExistsException(final UUID keycloakSubjectId, final Throwable cause) {
        super(IdentityErrorCode.JM_USER_ALREADY_EXISTS, "jm_user already exists for keycloakSubjectId=" + keycloakSubjectId, cause);
    }

}
