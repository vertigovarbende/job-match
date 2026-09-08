package com.deveyk.jobmatch.audit.unit.domain.model;

import com.deveyk.jobmatch.audit.domain.AuditAction;
import com.deveyk.jobmatch.audit.domain.event.AuditableDomainEvent;
import com.deveyk.jobmatch.audit.domain.model.AuditLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link AuditLog#from(AuditableDomainEvent)} için birim testleri: hem happy-path alan
 * eşlemesini hem de constructor'daki null/blank validasyonlarını doğrular.
 */
@DisplayName("AuditLog - Birim Testleri")
class AuditLogTest {

    private static final AuditAction ACTION = new AuditAction() {
        @Override
        public String code() {
            return "TEST_ACTION";
        }

        @Override
        public String description() {
            return "Test amaçlı sahte audit action";
        }
    };

    @Test
    @DisplayName("from() event'teki tüm alanları doğru şekilde eşler")
    void from_mapsAllFieldsFromEvent() {
        final Instant occurredAt = Instant.parse("2026-01-01T00:00:00Z");
        final AuditableDomainEvent event = eventWith(
                "actor-1", "ADMIN", ACTION, "JOB", "job-1", "corr-1", Map.of("k", "v"), occurredAt);

        final AuditLog auditLog = AuditLog.from(event);

        assertThat(auditLog.getActorId()).isEqualTo("actor-1");
        assertThat(auditLog.getActorRole()).isEqualTo("ADMIN");
        assertThat(auditLog.getAction()).isEqualTo(ACTION);
        assertThat(auditLog.getTargetType()).isEqualTo("JOB");
        assertThat(auditLog.getTargetId()).isEqualTo("job-1");
        assertThat(auditLog.getCorrelationId()).isEqualTo("corr-1");
        assertThat(auditLog.getDetails()).isEqualTo(Map.of("k", "v"));
        assertThat(auditLog.getOccurredAt()).isEqualTo(occurredAt);
    }

    @Test
    @DisplayName("from() null actorRole ve correlationId'ye izin verir")
    void from_allowsNullActorRoleAndCorrelationId() {
        final AuditableDomainEvent event = eventWith(
                "actor-1", null, ACTION, "JOB", "job-1", null, null, Instant.now());

        final AuditLog auditLog = AuditLog.from(event);

        assertThat(auditLog.getActorRole()).isNull();
        assertThat(auditLog.getCorrelationId()).isNull();
        assertThat(auditLog.getDetails()).isNull();
    }

    @Test
    @DisplayName("from() event null olduğunda NullPointerException fırlatır")
    void from_throwsNullPointerException_whenEventIsNull() {
        assertThatThrownBy(() -> AuditLog.from(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("event");
    }

    @Test
    @DisplayName("from() action null olduğunda NullPointerException fırlatır")
    void from_throwsNullPointerException_whenActionIsNull() {
        final AuditableDomainEvent event = eventWith("actor-1", null, null, "JOB", "job-1", null, null, Instant.now());

        assertThatThrownBy(() -> AuditLog.from(event))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("action");
    }

    @Test
    @DisplayName("from() targetType null olduğunda IllegalArgumentException fırlatır")
    void from_throwsIllegalArgumentException_whenTargetTypeIsNull() {
        final AuditableDomainEvent event = eventWith("actor-1", null, ACTION, null, "job-1", null, null, Instant.now());

        assertThatThrownBy(() -> AuditLog.from(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("targetType");
    }

    @Test
    @DisplayName("from() targetType boş (blank) olduğunda IllegalArgumentException fırlatır")
    void from_throwsIllegalArgumentException_whenTargetTypeIsBlank() {
        final AuditableDomainEvent event = eventWith("actor-1", null, ACTION, "   ", "job-1", null, null, Instant.now());

        assertThatThrownBy(() -> AuditLog.from(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("targetType");
    }

    @Test
    @DisplayName("from() targetId null olduğunda IllegalArgumentException fırlatır")
    void from_throwsIllegalArgumentException_whenTargetIdIsNull() {
        final AuditableDomainEvent event = eventWith("actor-1", null, ACTION, "JOB", null, null, null, Instant.now());

        assertThatThrownBy(() -> AuditLog.from(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("targetId");
    }

    @Test
    @DisplayName("from() targetId boş (blank) olduğunda IllegalArgumentException fırlatır")
    void from_throwsIllegalArgumentException_whenTargetIdIsBlank() {
        final AuditableDomainEvent event = eventWith("actor-1", null, ACTION, "JOB", "   ", null, null, Instant.now());

        assertThatThrownBy(() -> AuditLog.from(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("targetId");
    }

    @Test
    @DisplayName("from() occurredAt null olduğunda NullPointerException fırlatır")
    void from_throwsNullPointerException_whenOccurredAtIsNull() {
        final AuditableDomainEvent event = eventWith("actor-1", null, ACTION, "JOB", "job-1", null, null, null);

        assertThatThrownBy(() -> AuditLog.from(event))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("occurredAt");
    }

    private static AuditableDomainEvent eventWith(String actorId,
                                                   String actorRole,
                                                   AuditAction action,
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
                return action;
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
