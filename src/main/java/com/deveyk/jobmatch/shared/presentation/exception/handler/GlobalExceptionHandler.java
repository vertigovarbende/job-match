package com.deveyk.jobmatch.shared.presentation.exception.handler;

import com.deveyk.jobmatch.shared.domain.exception.DomainRuleViolationException;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchAuthenticationException;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchConflictException;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchException;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchForbiddenException;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchInvalidArgumentException;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchProcessException;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchResourceNotFoundException;
import com.deveyk.jobmatch.shared.presentation.exception.CommonErrorCode;
import com.deveyk.jobmatch.shared.presentation.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===== Katman 1 — Framework exception'ları (statik statü) =====

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {

        final ErrorResponse errorResponse = ErrorResponse.subErrors(exception.getBindingResult().getFieldErrors())
                .header(CommonErrorCode.VALIDATION_ERROR.header())
                .code(CommonErrorCode.VALIDATION_ERROR.code())
                .message(CommonErrorCode.VALIDATION_ERROR.defaultMessage())
                .build();

        this.logException(exception, errorResponse);

        return errorResponse;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException exception) {

        final ErrorResponse errorResponse = ErrorResponse.subErrors(exception.getConstraintViolations())
                .header(CommonErrorCode.VALIDATION_ERROR.header())
                .code(CommonErrorCode.VALIDATION_ERROR.code())
                .message(CommonErrorCode.VALIDATION_ERROR.defaultMessage())
                .build();

        this.logException(exception, errorResponse);

        return errorResponse;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException exception) {

        final ErrorResponse errorResponse = ErrorResponse.subErrors(exception)
                .header(CommonErrorCode.VALIDATION_ERROR.header())
                .code(CommonErrorCode.VALIDATION_ERROR.code())
                .message(CommonErrorCode.VALIDATION_ERROR.defaultMessage())
                .build();

        this.logException(exception, errorResponse);

        return errorResponse;
    }


    // ===== Katman 2 — Aile bazlı custom exception'lar (statik statü, ErrorCode instance'tan okunur) =====

    @ExceptionHandler(DomainRuleViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleDomainRuleViolationException(final DomainRuleViolationException exception) {

        final ErrorResponse errorResponse = this.toErrorResponse(exception);

        this.logException(exception, errorResponse);

        return errorResponse;
    }

    @ExceptionHandler(JobMatchResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleJobMatchResourceNotFoundException(final JobMatchResourceNotFoundException exception) {

        final ErrorResponse errorResponse = this.toErrorResponse(exception);

        this.logException(exception, errorResponse);

        return errorResponse;
    }

    @ExceptionHandler(JobMatchConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleJobMatchConflictException(final JobMatchConflictException exception) {

        final ErrorResponse errorResponse = this.toErrorResponse(exception);

        this.logException(exception, errorResponse);

        return errorResponse;
    }

    @ExceptionHandler(JobMatchForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleJobMatchForbiddenException(final JobMatchForbiddenException exception) {

        final ErrorResponse errorResponse = this.toErrorResponse(exception);

        this.logException(exception, errorResponse);

        return errorResponse;
    }

    @ExceptionHandler(JobMatchAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleJobMatchAuthenticationException(final JobMatchAuthenticationException exception) {

        final ErrorResponse errorResponse = this.toErrorResponse(exception);

        this.logException(exception, errorResponse);

        return errorResponse;
    }

    @ExceptionHandler(JobMatchProcessException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleJobMatchProcessException(final JobMatchProcessException exception) {

        final ErrorResponse errorResponse = this.toErrorResponse(exception);

        this.logException(exception, errorResponse);

        return errorResponse;
    }

    @ExceptionHandler(JobMatchInvalidArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleJobMatchInvalidArgumentException(final JobMatchInvalidArgumentException exception) {

        final ErrorResponse errorResponse = this.toErrorResponse(exception);

        this.logException(exception, errorResponse);

        return errorResponse;
    }


    // ===== Katman 3 — Genel JobMatchException fallback'i (dinamik statü) =====

    @ExceptionHandler(JobMatchException.class)
    public ResponseEntity<ErrorResponse> handleJobMatchException(final JobMatchException exception) {

        final ErrorResponse errorResponse = this.toErrorResponse(exception);

        this.logException(exception, errorResponse);

        return ResponseEntity.status(HttpStatus.valueOf(exception.getErrorCode().status())).body(errorResponse);
    }


    // ===== Katman 4 — Gerçekten beklenmeyen exception'lar (statik statü) =====

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception exception) {

        final ErrorResponse errorResponse = ErrorResponse.builder()
                .header(CommonErrorCode.INTERNAL_SERVER_ERROR.header())
                .code(CommonErrorCode.INTERNAL_SERVER_ERROR.code())
                .message(CommonErrorCode.INTERNAL_SERVER_ERROR.defaultMessage())
                .build();

        this.logException(exception, errorResponse);

        return errorResponse;
    }


    private ErrorResponse toErrorResponse(final JobMatchException exception) {

        return ErrorResponse.builder()
                .header(exception.getErrorCode().header())
                .code(exception.getErrorCode().code())
                .message(exception.getMessage())
                .build();
    }

    private void logException(final Exception exception, final ErrorResponse errorResponse) {

        final String responseCode = errorResponse.getCode();
        log.error("responseCode:{} | {}", responseCode, exception.getMessage());
        log.trace("responseCode:{} | StackTrace:", responseCode, exception);

    }

}
