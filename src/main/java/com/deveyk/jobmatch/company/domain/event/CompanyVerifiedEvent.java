package com.deveyk.jobmatch.company.domain.event;

import com.deveyk.jobmatch.audit.domain.AuditAction;
import com.deveyk.jobmatch.audit.domain.event.AuditableDomainEvent;
import com.deveyk.jobmatch.company.domain.CompanyAuditAction;

import java.time.Instant;

public record CompanyVerifiedEvent(String actorId, String targetId, Instant occurredAt) implements AuditableDomainEvent {

    public CompanyVerifiedEvent(final String actorId, final String targetId) {
        this(actorId, targetId, Instant.now());
    }

    @Override
    public AuditAction action() {
        return CompanyAuditAction.VERIFIED;
    }

    @Override
    public String targetType() {
        return "COMPANY";
    }

    @Override
    public Object details() {
        return null;
    }

}
