package com.deveyk.jobmatch.audit.domain.model;

import com.deveyk.jobmatch.audit.domain.AuditAction;
import com.deveyk.jobmatch.audit.domain.event.AuditableDomainEvent;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * {@code audit_log}'a yazılacak bir kaydın framework'ten bağımsız domain temsili.
 * <p>
 * Bir {@link AuditableDomainEvent}'ten türetilir ({@link #from(AuditableDomainEvent)}); JPA
 * persistence entity'sinden ({@code AuditLogEntity}) bilinçli olarak ayrı tutulur (bkz.
 * ARCHITECTURE.md #2, ADR-010) — ikisi arasındaki mapping infrastructure katmanında MapStruct
 * ile yapılır.
 */
@Getter
public final class AuditLog {

    private final String actorId;
    private final String actorRole;
    private final AuditAction action;
    private final String targetType;
    private final String targetId;
    private final String correlationId;
    private final Object details;
    private final Instant occurredAt;

    private AuditLog(String actorId,
                      String actorRole,
                      AuditAction action,
                      String targetType,
                      String targetId,
                      String correlationId,
                      Object details,
                      Instant occurredAt) {
        this.actorId = actorId;
        this.actorRole = actorRole;
        this.action = Objects.requireNonNull(action, "action must not be null");
        this.targetType = requireNonBlank(targetType, "targetType");
        this.targetId = requireNonBlank(targetId, "targetId");
        this.correlationId = correlationId;
        this.details = details;
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt must not be null");
    }

    public static AuditLog from(AuditableDomainEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        return new AuditLog(
                event.actorId(),
                event.actorRole(),
                event.action(),
                event.targetType(),
                event.targetId(),
                event.correlationId(),
                event.details(),
                event.occurredAt()
        );
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }

}
