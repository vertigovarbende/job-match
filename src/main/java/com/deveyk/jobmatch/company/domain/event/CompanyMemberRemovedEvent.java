package com.deveyk.jobmatch.company.domain.event;

import com.deveyk.jobmatch.audit.domain.AuditAction;
import com.deveyk.jobmatch.audit.domain.event.AuditableDomainEvent;
import com.deveyk.jobmatch.company.domain.CompanyAuditAction;

import java.time.Instant;
import java.util.Map;

public record CompanyMemberRemovedEvent(String actorId, String targetId, Long userId, Instant occurredAt) implements AuditableDomainEvent {

    public CompanyMemberRemovedEvent(final String actorId, final String targetId, final Long userId) {
        this(actorId, targetId, userId, Instant.now());
    }

    @Override
    public AuditAction action() {
        return CompanyAuditAction.MEMBER_REMOVED;
    }

    @Override
    public String targetType() {
        return "COMPANY";
    }

    @Override
    public Object details() {
        return Map.of("userId", this.userId);
    }

}
