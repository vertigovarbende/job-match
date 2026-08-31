package com.deveyk.jobmatch.shared.domain.exception;

import com.deveyk.jobmatch.shared.domain.ErrorCode;
import java.io.Serial;

/**
 * Kimlik doğrulama hatası ailesi. HTTP 401 ile eşlenir.
 * Doğrudan fırlatılmaz — her modül kendi somut exception'ını bu sınıftan türetir. Bkz. ADR-011.
 */
public abstract class JobMatchAuthenticationException extends JobMatchException {

    @Serial
    private static final long serialVersionUID = 1L;

    protected JobMatchAuthenticationException(final ErrorCode errorCode) {
        super(errorCode);
    }

    protected JobMatchAuthenticationException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    protected JobMatchAuthenticationException(final ErrorCode errorCode, final String message, final Throwable cause) {
        super(errorCode, message, cause);
    }

}
