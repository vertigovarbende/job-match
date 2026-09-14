package com.deveyk.jobmatch.audit.integration;

import com.deveyk.jobmatch.audit.domain.AuditAction;
import com.deveyk.jobmatch.audit.domain.event.AuditableDomainEvent;

import java.time.Instant;

record FakeAuditableEvent(String targetId, Object details) implements AuditableDomainEvent {

    FakeAuditableEvent(String targetId) {
        this(targetId, null);
    }

    @Override
    public String actorId() {
        return "test-actor";
    }

    @Override
    public AuditAction action() {
        return FakeAuditAction.TEST_ACTION;
    }

    @Override
    public String targetType() {
        return "TEST_TARGET";
    }

    @Override
    public Instant occurredAt() {
        return Instant.now();
    }

}
