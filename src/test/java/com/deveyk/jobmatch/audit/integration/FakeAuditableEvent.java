package com.deveyk.jobmatch.audit.integration;

import com.deveyk.jobmatch.audit.domain.AuditAction;
import com.deveyk.jobmatch.audit.domain.event.AuditableDomainEvent;

import java.time.Instant;

/**
 * Testlerde kullanılan sahte {@link AuditableDomainEvent} implementasyonu. Gerçek bir feature
 * event'i (ör. JobPublishedEvent) henüz mevcut olmadığından, generic audit mekanizmasını
 * (listener -> repository -> entity) izole şekilde doğrulamak için kullanılır.
 * <p>
 * İsim kasıtlı olarak {@code Fake} önekiyle başlar, {@code Test} önekiyle değil: Maven
 * Surefire/Failsafe'in varsayılan include pattern'i ({@code **&#47;Test*.java}) bu dosyayı
 * yanlışlıkla bir test sınıfı sanıp çalıştırmaya çalışmasın diye.
 * <p>
 * {@code details} varsayılan olarak {@code null}'dır ({@link #FakeAuditableEvent(String)});
 * JSONB {@code details} kolonunun round-trip'ini test etmek gibi durumlarda ikinci
 * constructor ({@link #FakeAuditableEvent(String, Object)}) ile açıkça verilebilir.
 */
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
