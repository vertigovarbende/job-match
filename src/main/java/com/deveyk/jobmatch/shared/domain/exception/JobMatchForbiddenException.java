package com.deveyk.jobmatch.shared.domain.exception;

import com.deveyk.jobmatch.shared.domain.ErrorCode;
import java.io.Serial;

/**
 * Yetkilendirme hatası ailesi. HTTP 403 ile eşlenir.
 * Doğrudan fırlatılmaz — her modül kendi somut exception'ını bu sınıftan türetir. Bkz. ADR-011.
 */
public abstract class JobMatchForbiddenException extends JobMatchException {

    @Serial
    private static final long serialVersionUID = 1L;

    protected JobMatchForbiddenException(final ErrorCode errorCode) {
        super(errorCode);
    }

    protected JobMatchForbiddenException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    protected JobMatchForbiddenException(final ErrorCode errorCode, final String message, final Throwable cause) {
        super(errorCode, message, cause);
    }

}
