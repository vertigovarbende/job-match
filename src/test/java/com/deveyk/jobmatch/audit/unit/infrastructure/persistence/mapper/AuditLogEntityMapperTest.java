package com.deveyk.jobmatch.audit.unit.infrastructure.persistence.mapper;

import com.deveyk.jobmatch.audit.domain.AuditAction;
import com.deveyk.jobmatch.audit.domain.event.AuditableDomainEvent;
import com.deveyk.jobmatch.audit.domain.model.AuditLog;
import com.deveyk.jobmatch.audit.infrastructure.persistence.entity.AuditLogEntity;
import com.deveyk.jobmatch.audit.infrastructure.persistence.mapper.AuditDetailsConverter;
import com.deveyk.jobmatch.audit.infrastructure.persistence.mapper.AuditLogEntityMapper;
import com.deveyk.jobmatch.audit.infrastructure.persistence.mapper.AuditLogEntityMapperImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link AuditLogEntityMapper} (MapStruct tarafından üretilen {@link AuditLogEntityMapperImpl})
 * için birim testleri. Spring context'e ihtiyaç olmadan test edilebilmesi, mapper'ın
 * {@code injectionStrategy = InjectionStrategy.CONSTRUCTOR} olarak tanımlanmasına dayanır.
 */
@DisplayName("AuditLogEntityMapper - Birim Testleri")
class AuditLogEntityMapperTest {

    private static final AuditAction ACTION = new AuditAction() {
        @Override
        public String code() {
            return "JOB_PUBLISHED";
        }

        @Override
        public String description() {
            return "Test amaçlı sahte audit action";
        }
    };

    private final ObjectMapper objectMapper = JsonMapper.builder().build();
    private final AuditDetailsConverter auditDetailsConverter = new AuditDetailsConverter(this.objectMapper);
    private final AuditLogEntityMapper mapper = new AuditLogEntityMapperImpl(this.auditDetailsConverter);

    @Test
    @DisplayName("toEntity() tüm alanları eşler ve id'yi yoksayar")
    void toEntity_mapsAllFieldsAndIgnoresId() {
        final Instant occurredAt = Instant.parse("2026-01-01T00:00:00Z");
        final AuditLog auditLog = AuditLog.from(eventWith(
                "actor-1", "ADMIN", "JOB", "job-1", "corr-1", Map.of("k", "v"), occurredAt));

        final AuditLogEntity entity = this.mapper.toEntity(auditLog);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getActorId()).isEqualTo("actor-1");
        assertThat(entity.getActorRole()).isEqualTo("ADMIN");
        assertThat(entity.getAction()).isEqualTo("JOB_PUBLISHED");
        assertThat(entity.getTargetType()).isEqualTo("JOB");
        assertThat(entity.getTargetId()).isEqualTo("job-1");
        assertThat(entity.getCorrelationId()).isEqualTo("corr-1");
        assertThat(entity.getDetails()).containsEntry("k", "v");
        assertThat(entity.getOccurredAt()).isEqualTo(occurredAt);
    }

    @Test
    @DisplayName("toEntity() null details ve null correlationId'yi doğru eşler")
    void toEntity_mapsNullDetailsAndNullCorrelationId() {
        final AuditLog auditLog = AuditLog.from(eventWith(
                "actor-1", null, "JOB", "job-1", null, null, Instant.now()));

        final AuditLogEntity entity = this.mapper.toEntity(auditLog);

        assertThat(entity.getDetails()).isNull();
        assertThat(entity.getCorrelationId()).isNull();
        assertThat(entity.getActorRole()).isNull();
    }

    private static AuditableDomainEvent eventWith(String actorId,
                                                   String actorRole,
                                                   String targetType,
                                                   String targetId,
                                                   String correlationId,
                                                   Object details,
                                                   Instant occurredAt) {
        return new AuditableDomainEvent() {
            @Override
            public String actorId() {
                return actorId;
            }

            @Override
            public String actorRole() {
                return actorRole;
            }

            @Override
            public AuditAction action() {
                return ACTION;
            }

            @Override
            public String targetType() {
                return targetType;
            }

            @Override
            public String targetId() {
                return targetId;
            }

            @Override
            public String correlationId() {
                return correlationId;
            }

            @Override
            public Object details() {
                return details;
            }

            @Override
            public Instant occurredAt() {
                return occurredAt;
            }
        };
    }

}
