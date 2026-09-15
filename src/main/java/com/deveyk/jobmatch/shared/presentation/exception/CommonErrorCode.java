package com.deveyk.jobmatch.shared.presentation.exception;

import com.deveyk.jobmatch.shared.domain.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

/**
 * Herhangi bir modüle (job, candidate, company, application, matching, identity) özgü olmayan,
 * framework/altyapı seviyesinde oluşan genel hata kodlarını temsil eder.
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    INTERNAL_SERVER_ERROR(
            "GEN_001",
            "INTERNAL_SERVER_ERROR",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Unexpected server error."
    ),

    VALIDATION_ERROR(
            "GEN_002",
            "VALIDATION_ERROR",
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed."
    ),

    ACCESS_DENIED(
            "GEN_003",
            "ACCESS_DENIED",
            HttpStatus.FORBIDDEN.value(),
            "You do not have permission to perform this operation."
    ),

    AUTHENTICATION_REQUIRED(
            "GEN_004",
            "AUTHENTICATION_REQUIRED",
            HttpStatus.UNAUTHORIZED.value(),
            "Authentication is required to access this resource."
    ),

    RESOURCE_NOT_FOUND("GEN_005",
            "RESOURCE_NOT_FOUND",
            HttpStatus.NOT_FOUND.value(),
            "The requested resource could not be found."
    ),

    METHOD_NOT_ALLOWED(
            "GEN_006",
            "METHOD_NOT_ALLOWED",
            HttpStatus.METHOD_NOT_ALLOWED.value(),
            "This HTTP method is not supported for this endpoint."
    ),

    UNSUPPORTED_MEDIA_TYPE(
            "GEN_007",
            "UNSUPPORTED_MEDIA_TYPE",
            HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
            "Unsupported content type."
    ),

    MALFORMED_REQUEST_BODY(
            "GEN_008",
            "MALFORMED_REQUEST_BODY",
            HttpStatus.BAD_REQUEST.value(),
            "The request body could not be read or is malformed."
    ),

    RATE_LIMIT_EXCEEDED(
            "GEN_009",
            "RATE_LIMIT_EXCEEDED",
            HttpStatus.TOO_MANY_REQUESTS.value(),
            "Too many requests. Please try again later."
    );

    private final String code;
    private final String header;
    private final int status;
    private final String defaultMessage;

}
