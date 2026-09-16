package com.deveyk.jobmatch.company.domain;

import com.deveyk.jobmatch.audit.domain.AuditAction;

public enum CompanyAuditAction implements AuditAction {

    VERIFIED;

    @Override
    public String code() {
        return name();
    }

    @Override
    public String description() {
        return "Company verified";
    }

}
