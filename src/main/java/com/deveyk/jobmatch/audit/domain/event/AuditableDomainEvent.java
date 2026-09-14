package com.deveyk.jobmatch.audit.domain.event;

import com.deveyk.jobmatch.audit.domain.AuditAction;

import java.time.Instant;

public interface AuditableDomainEvent {

    String actorId();

    default String actorRole() { return null; }

    AuditAction action();

    String targetType();

    String targetId();

    default String correlationId() { return null; }

    Object details();

    Instant occurredAt();

}
