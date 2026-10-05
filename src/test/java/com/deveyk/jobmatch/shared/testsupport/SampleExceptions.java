package com.deveyk.jobmatch.shared.testsupport;

import com.deveyk.jobmatch.shared.domain.ErrorCode;
import com.deveyk.jobmatch.shared.domain.exception.DomainRuleViolationException;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchAuthenticationException;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchConflictException;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchException;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchForbiddenException;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchInvalidArgumentException;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchProcessException;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchResourceNotFoundException;

import java.io.Serial;

/**
 * Hata isleme testleri icin her exception ailesinden (ve aileye oturmayan JobMatchException'dan)
 * birer somut test exception'i. Hepsi bir ErrorCode alir (bkz. SampleErrorCode).
 */
public final class SampleExceptions {

    private SampleExceptions() {
    }

    /**
     * Is kurali ihlali ailesi (422).
     */
    public static final class SampleDomainRuleViolationException extends DomainRuleViolationException {

        @Serial
        private static final long serialVersionUID = 1L;

        public SampleDomainRuleViolationException(final ErrorCode errorCode) {
            super(errorCode);
        }

    }

    /**
     * Kaynak bulunamadi ailesi (404).
     */
    public static final class SampleResourceNotFoundException extends JobMatchResourceNotFoundException {

        @Serial
        private static final long serialVersionUID = 1L;

        public SampleResourceNotFoundException(final ErrorCode errorCode) {
            super(errorCode);
        }

    }

    /**
     * Cakisma ailesi (409).
     */
    public static final class SampleConflictException extends JobMatchConflictException {

        @Serial
        private static final long serialVersionUID = 1L;

        public SampleConflictException(final ErrorCode errorCode) {
            super(errorCode);
        }

    }

    /**
     * Yetki yok ailesi (403).
     */
    public static final class SampleForbiddenException extends JobMatchForbiddenException {

        @Serial
        private static final long serialVersionUID = 1L;

        public SampleForbiddenException(final ErrorCode errorCode) {
            super(errorCode);
        }

    }

    /**
     * Kimlik dogrulama ailesi (401).
     */
    public static final class SampleAuthenticationException extends JobMatchAuthenticationException {

        @Serial
        private static final long serialVersionUID = 1L;

        public SampleAuthenticationException(final ErrorCode errorCode) {
            super(errorCode);
        }

    }

    /**
     * Surec/state-machine ihlali ailesi (409).
     */
    public static final class SampleProcessException extends JobMatchProcessException {

        @Serial
        private static final long serialVersionUID = 1L;

        public SampleProcessException(final ErrorCode errorCode) {
            super(errorCode);
        }

    }

    /**
     * Gecersiz arguman ailesi (400).
     */
    public static final class SampleInvalidArgumentException extends JobMatchInvalidArgumentException {

        @Serial
        private static final long serialVersionUID = 1L;

        public SampleInvalidArgumentException(final ErrorCode errorCode) {
            super(errorCode);
        }

    }

    /**
     * Hicbir aileye oturmayan, dogrudan JobMatchException'dan turetilmis exception.
     */
    public static final class SampleUnclassifiedException extends JobMatchException {

        @Serial
        private static final long serialVersionUID = 1L;

        public SampleUnclassifiedException(final ErrorCode errorCode) {
            super(errorCode);
        }

    }

}
