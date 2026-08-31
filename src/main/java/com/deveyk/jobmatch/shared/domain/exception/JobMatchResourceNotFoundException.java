package com.deveyk.jobmatch.shared.domain.exception;

import com.deveyk.jobmatch.shared.domain.ErrorCode;
import java.io.Serial;

/**
 * "Kaynak bulunamadı" ailesi. HTTP 404 ile eşlenir.
 * Doğrudan fırlatılmaz — her modül kendi somut exception'ını bu sınıftan türetir. Bkz. ADR-011.
 */
public abstract class JobMatchResourceNotFoundException extends JobMatchException {

    @Serial
    private static final long serialVersionUID = 1L;

    protected JobMatchResourceNotFoundException(final ErrorCode errorCode) {
        super(errorCode);
    }

    protected JobMatchResourceNotFoundException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    protected JobMatchResourceNotFoundException(final ErrorCode errorCode, final String message, final Throwable cause) {
        super(errorCode, message, cause);
    }

}
