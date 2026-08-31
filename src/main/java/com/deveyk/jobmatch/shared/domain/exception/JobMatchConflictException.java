package com.deveyk.jobmatch.shared.domain.exception;

import com.deveyk.jobmatch.shared.domain.ErrorCode;
import java.io.Serial;

/**
 * Kaynak çakışması ailesi (ör. aynı email ile ikinci kayıt). HTTP 409 ile eşlenir.
 * Doğrudan fırlatılmaz — her modül kendi somut exception'ını bu sınıftan türetir. Bkz. ADR-011.
 */
public abstract class JobMatchConflictException extends JobMatchException {

    @Serial
    private static final long serialVersionUID = 1L;

    protected JobMatchConflictException(final ErrorCode errorCode) {
        super(errorCode);
    }

    protected JobMatchConflictException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    protected JobMatchConflictException(final ErrorCode errorCode, final String message, final Throwable cause) {
        super(errorCode, message, cause);
    }

}
