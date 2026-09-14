package com.deveyk.jobmatch.identity.domain.exception;

import com.deveyk.jobmatch.shared.domain.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum IdentityErrorCode implements ErrorCode {

    INVALID_BUSINESS_ROLE_CLAIM(
            "IDN_001",
            "INVALID_BUSINESS_ROLE_CLAIM",
            422,
            "JWT must contain exactly one business role (CANDIDATE, EMPLOYER, or ADMIN)."
    ),

    JM_USER_ALREADY_EXISTS(
            "IDN_002",
            "JM_USER_ALREADY_EXISTS",
            409,
            "A jm_user record already exists for this Keycloak subject id."
    );

    private final String code;
    private final String header;
    private final int status;
    private final String defaultMessage;

}
