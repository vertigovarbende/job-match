package com.deveyk.jobmatch.job.domain;

import com.deveyk.jobmatch.shared.domain.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum JobErrorCode implements ErrorCode {

    INVALID_JOB_STATUS_TRANSITION(
            "JOB_001",
            "INVALID_JOB_STATUS_TRANSITION",
            422,
            "The requested action is not valid for the job's current status."
    ),

    JOB_FIELD_INVALID(
            "JOB_002",
            "JOB_FIELD_INVALID",
            400,
            "A required job field is missing or invalid."
    ),

    DUPLICATE_SKILL_REFERENCE(
            "JOB_003",
            "DUPLICATE_SKILL_REFERENCE",
            422,
            "A skill cannot be both required and preferred on the same job."
    ),

    JOB_NOT_READY_FOR_PUBLISH(
            "JOB_004",
            "JOB_NOT_READY_FOR_PUBLISH",
            422,
            "The job is missing fields required before it can be published."
    ),

    JOB_NOT_FOUND(
            "JOB_005",
            "JOB_NOT_FOUND",
            404,
            "No job found for the given id."
    ),

    COMPANY_MEMBERSHIP_REQUIRED(
            "JOB_006",
            "COMPANY_MEMBERSHIP_REQUIRED",
            403,
            "You must belong to a company before creating a job."
    );

    private final String code;
    private final String header;
    private final int status;
    private final String defaultMessage;

}
