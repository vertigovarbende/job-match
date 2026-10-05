package com.deveyk.jobmatch.shared.presentation.exception;

import com.deveyk.jobmatch.shared.domain.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

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
            "Unexpected server error."
    ),

    VALIDATION_ERROR(
            "GEN_002",
            "VALIDATION_ERROR",
            "Validation failed."
    ),

    ACCESS_DENIED(
            "GEN_003",
            "ACCESS_DENIED",
            "You do not have permission to perform this operation."
    ),

    AUTHENTICATION_REQUIRED(
            "GEN_004",
            "AUTHENTICATION_REQUIRED",
            "Authentication is required to access this resource."
    ),

    RESOURCE_NOT_FOUND("GEN_005",
            "RESOURCE_NOT_FOUND",
            "The requested resource could not be found."
    ),

    METHOD_NOT_ALLOWED(
            "GEN_006",
            "METHOD_NOT_ALLOWED",
            "This HTTP method is not supported for this endpoint."
    ),

    UNSUPPORTED_MEDIA_TYPE(
            "GEN_007",
            "UNSUPPORTED_MEDIA_TYPE",
            "Unsupported content type."
    ),

    MALFORMED_REQUEST_BODY(
            "GEN_008",
            "MALFORMED_REQUEST_BODY",
            "The request body could not be read or is malformed."
    ),

    RATE_LIMIT_EXCEEDED(
            "GEN_009",
            "RATE_LIMIT_EXCEEDED",
            "Too many requests. Please try again later."
    );

    private final String code;
    private final String header;
    private final String defaultMessage;

}
