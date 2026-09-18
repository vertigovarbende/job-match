package com.deveyk.jobmatch.job.domain.event;

import com.deveyk.jobmatch.audit.domain.AuditAction;
import com.deveyk.jobmatch.audit.domain.event.AuditableDomainEvent;
import com.deveyk.jobmatch.job.domain.JobAuditAction;
import com.deveyk.jobmatch.search.domain.event.IndexOperation;
import com.deveyk.jobmatch.search.domain.event.SearchIndexableEvent;

import java.time.Instant;

public record JobClosedEvent(String actorId, String targetId, Instant occurredAt) implements AuditableDomainEvent, SearchIndexableEvent {

    public JobClosedEvent(final String actorId, final String targetId) {
        this(actorId, targetId, Instant.now());
    }

    @Override
    public AuditAction action() {
        return JobAuditAction.CLOSED;
    }

    @Override
    public String targetType() {
        return "JOB";
    }

    @Override
    public IndexOperation operation() {
        return IndexOperation.DELETE;
    }

    @Override
    public Object details() {
        return null;
    }

}
