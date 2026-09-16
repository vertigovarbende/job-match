package com.deveyk.jobmatch.company.domain;

import com.deveyk.jobmatch.audit.domain.AuditAction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum CompanyAuditAction implements AuditAction {

    VERIFIED("Company verified"),
    MEMBER_ADDED("Company member added"),
    MEMBER_REMOVED("Company member removed");

    private final String description;

    @Override
    public String code() {
        return name();
    }

}
