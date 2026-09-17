package com.deveyk.jobmatch.job.domain.event;

import com.deveyk.jobmatch.audit.domain.AuditAction;
import com.deveyk.jobmatch.audit.domain.event.AuditableDomainEvent;
import com.deveyk.jobmatch.job.domain.JobAuditAction;

import java.time.Instant;
import java.util.Map;

public record JobExpiredEvent(String targetId, Instant occurredAt) implements AuditableDomainEvent {

    public JobExpiredEvent(final String targetId) {
        this(targetId, Instant.now());
    }

    @Override
    public String actorId() {
        return "SYSTEM";
    }

    @Override
    public AuditAction action() {
        return JobAuditAction.EXPIRED;
    }

    @Override
    public String targetType() {
        return "JOB";
    }

    @Override
    public Object details() {
        return Map.of("trigger", "scheduled-expiry");
    }

}
