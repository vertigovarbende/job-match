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

    CANDIDATE_FIELD_INVALID(
            "CND_005",
            "CANDIDATE_FIELD_INVALID",
            400,
            "A required candidate profile field is missing or invalid."
    ),

    CANDIDATE_RESOURCE_NOT_FOUND(
            "CND_006",
            "CANDIDATE_RESOURCE_NOT_FOUND",
            404,
            "The requested candidate-owned resource was not found."
    ),

    CANDIDATE_RESOURCE_FORBIDDEN(
            "CND_007",
            "CANDIDATE_RESOURCE_FORBIDDEN",
            403,
            "The requested resource does not belong to the candidate."
    ),

    CANDIDATE_RELATION_ALREADY_EXISTS(
            "CND_008",
            "CANDIDATE_RELATION_ALREADY_EXISTS",
            409,
            "This relation already exists for the candidate."
    );

    private final String code;
    private final String header;
    private final int status;
    private final String defaultMessage;

}