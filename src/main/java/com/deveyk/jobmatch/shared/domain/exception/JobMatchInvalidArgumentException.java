package com.deveyk.jobmatch.shared.domain.exception;

import com.deveyk.jobmatch.shared.domain.ErrorCode;
import java.io.Serial;

/**
 * Domain seviyesinde fırlatılan geçersiz argüman ailesi (framework seviyesi Bean Validation'dan ayrı). HTTP 400 ile eşlenir.
 * Doğrudan fırlatılmaz — her modül kendi somut exception'ını bu sınıftan türetir. Bkz. ADR-011.
 */
public abstract class JobMatchInvalidArgumentException extends JobMatchException {

    @Serial
    private static final long serialVersionUID = 1L;

    protected JobMatchInvalidArgumentException(final ErrorCode errorCode) {
        super(errorCode);
    }

    protected JobMatchInvalidArgumentException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    protected JobMatchInvalidArgumentException(final ErrorCode errorCode, final String message, final Throwable cause) {
        super(errorCode, message, cause);
    }

}
