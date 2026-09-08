package com.deveyk.jobmatch.audit.domain.event;

import com.deveyk.jobmatch.audit.domain.AuditAction;

import java.time.Instant;

/**
 * Feature'ların fırlattığı domain event'lerin, generic audit altyapısı tarafından otomatik olarak
 * yakalanıp {@code audit_log}'a yazılabilmesi için implemente ettiği marker interface.
 * <p>
 * Yeni bir aksiyonu audit'e eklemek, yeni bir listener/servis kodu yazmayı değil, yalnızca
 * ilgili event sınıfına bu interface'i implemente ettirmeyi gerektirir (bkz. docs/AUDIT.md).
 */
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
