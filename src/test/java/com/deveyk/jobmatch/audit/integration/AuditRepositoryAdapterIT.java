package com.deveyk.jobmatch.audit.integration;

import com.deveyk.jobmatch.audit.domain.model.AuditLog;
import com.deveyk.jobmatch.audit.infrastructure.persistence.adapter.AuditRepositoryAdapter;
import com.deveyk.jobmatch.audit.infrastructure.persistence.entity.AuditLogEntity;
import com.deveyk.jobmatch.audit.infrastructure.persistence.repository.SpringDataAuditJpaRepository;
import com.deveyk.jobmatch.testsupport.TestContainerConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("AuditRepositoryAdapter - Entegrasyon Testleri")
class AuditRepositoryAdapterIT extends TestContainerConfiguration {

    @Autowired
    private AuditRepositoryAdapter auditRepositoryAdapter;

    @Autowired
    private SpringDataAuditJpaRepository springDataAuditJpaRepository;

    private String targetId;

    @BeforeEach
    void setUp() {
        this.targetId = UUID.randomUUID().toString();
    }

    @Test
    @DisplayName("save() sonrası kayıt kendi id'si üzerinden geri okunabilir (auto-generate çalışır)")
    void save_persistsAuditLog_andIsReadableById() {
        // given
        final AuditLog auditLog = AuditLog.from(new FakeAuditableEvent(this.targetId));

        // when
        this.auditRepositoryAdapter.save(auditLog);

        // then
        final AuditLogEntity persisted = findByTargetId(this.targetId);
        assertThat(persisted.getId()).isNotNull();

        final AuditLogEntity reloaded = this.springDataAuditJpaRepository.findById(persisted.getId()).orElseThrow();
        assertThat(reloaded.getTargetId()).isEqualTo(this.targetId);
        assertThat(reloaded.getTargetType()).isEqualTo("TEST_TARGET");
        assertThat(reloaded.getAction()).isEqualTo("TEST_ACTION");
        assertThat(reloaded.getActorId()).isEqualTo("test-actor");
        assertThat(reloaded.getOccurredAt()).isNotNull();
    }

    @Test
    @DisplayName("Nested map/liste içeren details payload'ı JSONB round-trip'te bozulmadan korunur")
    void save_persistsNestedDetailsPayload_withoutDataLoss() {
        // given
        final Map<String, Object> details = Map.of(
                "oldStatus", "SUBMITTED",
                "newStatus", "UNDER_REVIEW",
                "reviewers", List.of("reviewer-1", "reviewer-2"),
                "metadata", Map.of("automatic", true, "triggeredBy", "system")
        );
        final AuditLog auditLog = AuditLog.from(new FakeAuditableEvent(this.targetId, details));

        // when
        this.auditRepositoryAdapter.save(auditLog);

        // then
        final AuditLogEntity persisted = findByTargetId(this.targetId);
        assertThat(persisted.getDetails()).isEqualTo(details);
    }

    private AuditLogEntity findByTargetId(String targetId) {
        return this.springDataAuditJpaRepository.findAll().stream()
                .filter(entity -> targetId.equals(entity.getTargetId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("audit_log'da targetId=" + targetId + " için kayıt bulunamadı"));
    }

}
