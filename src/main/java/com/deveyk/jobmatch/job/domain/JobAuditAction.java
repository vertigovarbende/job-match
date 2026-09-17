package com.deveyk.jobmatch.job.domain;

import com.deveyk.jobmatch.audit.domain.AuditAction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum JobAuditAction implements AuditAction {

    PUBLISHED("Job published"),
    CLOSED("Job closed"),
    ARCHIVED("Job archived"),
    EXPIRED("Job expired");

    private final String description;

    @Override
    public String code() {
        return name();
    }

}
