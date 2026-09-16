package com.deveyk.jobmatch.company.domain;

import com.deveyk.jobmatch.shared.domain.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum CompanyErrorCode implements ErrorCode {

    COMPANY_NOT_FOUND(
            "CMP_001",
            "COMPANY_NOT_FOUND",
            404,
            "No company found for the given id."
    ),

    COMPANY_ROLE_REQUIRED(
            "CMP_002",
            "COMPANY_ROLE_REQUIRED",
            403,
            "Only users with the EMPLOYER role can create a company."
    ),

    COMPANY_FIELD_INVALID(
            "CMP_003",
            "COMPANY_FIELD_INVALID",
            400,
            "A required company profile field is missing or invalid."
    ),

    COMPANY_MEMBERSHIP_ALREADY_EXISTS(
            "CMP_004",
            "COMPANY_MEMBERSHIP_ALREADY_EXISTS",
            409,
            "This employer already belongs to a company."
    ),

    COMPANY_MEMBERSHIP_NOT_FOUND(
            "CMP_005",
            "COMPANY_MEMBERSHIP_NOT_FOUND",
            404,
            "No company membership found for the given company and user."
    );

    private final String code;
    private final String header;
    private final int status;
    private final String defaultMessage;

}
