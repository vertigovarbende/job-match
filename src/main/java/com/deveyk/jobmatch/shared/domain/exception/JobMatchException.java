package com.deveyk.jobmatch.shared.domain.exception;

import com.deveyk.jobmatch.shared.domain.ErrorCode;
import java.io.Serial;

/**
 * Tüm custom (framework olmayan) exception'ların ortak, nötr kökü.
 * Doğrudan fırlatılmaz — her hata kategorisi için bir alt aile (bkz. DomainRuleViolationException,
 * JobMatchResourceNotFoundException vb.) extend edilir. Bkz. ADR-011.
 */
public abstract class JobMatchException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;

    protected JobMatchException(final ErrorCode errorCode) {
        super(errorCode.defaultMessage());
        this.errorCode = errorCode;
    }

    protected JobMatchException(final ErrorCode errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected JobMatchException(final ErrorCode errorCode, final String message, final Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
