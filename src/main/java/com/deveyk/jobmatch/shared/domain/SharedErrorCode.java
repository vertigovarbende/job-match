package com.deveyk.jobmatch.shared.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum SharedErrorCode implements ErrorCode {

    FIELD_INVALID(
            "SHR_001",
            "FIELD_INVALID",
            400,
            "A required field is missing or invalid."
    ),

    INVALID_SALARY_RANGE(
            "SHR_002",
            "INVALID_SALARY_RANGE",
            422,
            "Salary range minimum must not exceed maximum."
    );

    private final String code;
    private final String header;
    private final int status;
    private final String defaultMessage;

}
