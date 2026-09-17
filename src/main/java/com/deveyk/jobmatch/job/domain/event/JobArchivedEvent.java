package com.deveyk.jobmatch.job.domain.event;

import com.deveyk.jobmatch.audit.domain.AuditAction;
import com.deveyk.jobmatch.audit.domain.event.AuditableDomainEvent;
import com.deveyk.jobmatch.job.domain.JobAuditAction;

import java.time.Instant;

public record JobArchivedEvent(String actorId, String targetId, Instant occurredAt) implements AuditableDomainEvent {

    public JobArchivedEvent(final String actorId, final String targetId) {
        this(actorId, targetId, Instant.now());
    }

    @Override
    public AuditAction action() {
        return JobAuditAction.ARCHIVED;
    }

    @Override
    public String targetType() {
        return "JOB";
    }

    @Override
    public Object details() {
        return null;
    }

}
