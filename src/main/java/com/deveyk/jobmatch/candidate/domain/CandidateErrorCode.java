package com.deveyk.jobmatch.candidate.domain;

import com.deveyk.jobmatch.shared.domain.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum CandidateErrorCode implements ErrorCode {

    CANDIDATE_NOT_FOUND(
            "CND_001",
            "CANDIDATE_NOT_FOUND",
            404,
            "No candidate profile found for the given user."
    ),

    CANDIDATE_ALREADY_EXISTS(
            "CND_002",
            "CANDIDATE_ALREADY_EXISTS",
            409,
            "A candidate profile already exists for this user."
    ),

    CANDIDATE_ROLE_REQUIRED(
            "CND_003",
            "CANDIDATE_ROLE_REQUIRED",
            403,
            "Only users with the CANDIDATE role can own a candidate profile."
    ),

    INVALID_SALARY_RANGE(
            "CND_004",
            "INVALID_SALARY_RANGE",
            422,
            "Desired salary range minimum must not exceed maximum."
    ),

    CANDIDATE_FIELD_INVALID(
            "CND_005",
            "CANDIDATE_FIELD_INVALID",
            400,
            "A required candidate profile field is missing or invalid."
    );

    private final String code;
    private final String header;
    private final int status;
    private final String defaultMessage;

}