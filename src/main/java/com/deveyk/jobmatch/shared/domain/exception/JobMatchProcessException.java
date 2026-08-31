package com.deveyk.jobmatch.shared.domain.exception;

import com.deveyk.jobmatch.shared.domain.ErrorCode;
import java.io.Serial;

/**
 * Süreç/state-machine ihlalleri ailesi (ör. geçersiz durum geçişi). HTTP 409 ile eşlenir.
 * Doğrudan fırlatılmaz — her modül kendi somut exception'ını bu sınıftan türetir. Bkz. ADR-011.
 */
public abstract class JobMatchProcessException extends JobMatchException {

    @Serial
    private static final long serialVersionUID = 1L;

    protected JobMatchProcessException(final ErrorCode errorCode) {
        super(errorCode);
    }

    protected JobMatchProcessException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    protected JobMatchProcessException(final ErrorCode errorCode, final String message, final Throwable cause) {
        super(errorCode, message, cause);
    }

}
