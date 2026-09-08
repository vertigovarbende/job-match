package com.deveyk.jobmatch.audit.integration;

import com.deveyk.jobmatch.audit.infrastructure.persistence.entity.AuditLogEntity;
import com.deveyk.jobmatch.audit.infrastructure.persistence.repository.SpringDataAuditJpaRepository;
import com.deveyk.jobmatch.testsupport.TestContainerConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuditEventListener - Entegrasyon Testleri")
class AuditEventListenerIT extends TestContainerConfiguration {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private SpringDataAuditJpaRepository springDataAuditJpaRepository;

    private String targetId;
    private FakeAuditableEvent event;

    @BeforeEach
    void setUp() {
        this.targetId = UUID.randomUUID().toString();
        this.event = new FakeAuditableEvent(this.targetId);
    }

    @Test
    @DisplayName("Transaction commit olduğunda audit event audit_log'a yazılır")
    void auditableDomainEvent_isPersistedToAuditLog() {
        // when
        final TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.executeWithoutResult(status -> this.applicationEventPublisher.publishEvent(this.event));

        // then
        final List<AuditLogEntity> matches = this.springDataAuditJpaRepository.findAll().stream()
                .filter(entity -> this.targetId.equals(entity.getTargetId()))
                .toList();

        assertThat(matches).hasSize(1);

        final AuditLogEntity persisted = matches.get(0);
        assertThat(persisted.getAction()).isEqualTo("TEST_ACTION");
        assertThat(persisted.getTargetType()).isEqualTo("TEST_TARGET");
        assertThat(persisted.getActorId()).isEqualTo("test-actor");
        assertThat(persisted.getOccurredAt()).isNotNull();
    }

    @Test
    @DisplayName("Transaction rollback olduğunda audit event audit_log'a yazılmaz")
    void auditableDomainEvent_isNotPersisted_whenTransactionRollsBack() {
        // when
        final TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.executeWithoutResult(status -> {
            this.applicationEventPublisher.publishEvent(this.event);
            status.setRollbackOnly();
        });

        // then
        final List<AuditLogEntity> matches = this.springDataAuditJpaRepository.findAll().stream()
                .filter(entity -> this.targetId.equals(entity.getTargetId()))
                .toList();

        assertThat(matches).isEmpty();
    }

    @Test
    @DisplayName("Aktif transaction olmadan publish edilen audit event yine de audit_log'a yazılır (fallbackExecution)")
    void auditableDomainEvent_isPersisted_whenPublishedWithoutActiveTransaction() {
        // when
        this.applicationEventPublisher.publishEvent(this.event);

        // then
        final List<AuditLogEntity> matches = this.springDataAuditJpaRepository.findAll().stream()
                .filter(entity -> this.targetId.equals(entity.getTargetId()))
                .toList();

        assertThat(matches).hasSize(1);
    }

}
