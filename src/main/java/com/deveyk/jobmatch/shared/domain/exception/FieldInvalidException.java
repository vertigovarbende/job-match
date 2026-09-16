package com.deveyk.jobmatch.shared.domain.exception;

import com.deveyk.jobmatch.shared.domain.SharedErrorCode;

import java.io.Serial;

public class FieldInvalidException extends JobMatchInvalidArgumentException {

    @Serial
    private static final long serialVersionUID = 1L;

    public FieldInvalidException(final String fieldName) {
        super(SharedErrorCode.FIELD_INVALID, fieldName + " must not be blank");
    }

    public FieldInvalidException(final String fieldName, final String reason) {
        super(SharedErrorCode.FIELD_INVALID, fieldName + " " + reason);
    }

}
