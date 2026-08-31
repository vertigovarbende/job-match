package com.deveyk.jobmatch.shared.domain.exception;

import com.deveyk.jobmatch.shared.domain.ErrorCode;
import java.io.Serial;

/**
 * İş kuralı/invariant ihlalleri ailesi (ör. "yayınlanmış bir ilan tekrar yayınlanamaz"). HTTP 422 ile eşlenir.
 * Doğrudan fırlatılmaz — her modül kendi somut exception'ını bu sınıftan türetir. Bkz. ADR-011.
 */
public abstract class DomainRuleViolationException extends JobMatchException {

    @Serial
    private static final long serialVersionUID = 1L;

    protected DomainRuleViolationException(final ErrorCode errorCode) {
        super(errorCode);
    }

    protected DomainRuleViolationException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    protected DomainRuleViolationException(final ErrorCode errorCode, final String message, final Throwable cause) {
        super(errorCode, message, cause);
    }

}
