package com.deveyk.jobmatch.job.unit.domain.model;

import com.deveyk.jobmatch.job.domain.model.JobStatus;
import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JobStatus - Esitlik Testleri")
class JobStatusEqualityTest {

    @ParameterizedTest(name = "{0}")
    @EnumSource(JobStatusType.class)
    @DisplayName("ayni tipteki iki ayri instance esittir ve ayni hashCode'a sahiptir")
    void equals_returnsTrue_forSeparateInstancesOfSameType(final JobStatusType type) {

        final JobStatus first = JobStatus.of(type);
        final JobStatus second = JobStatus.of(type);

        assertThat(first).isEqualTo(second);
        assertThat(first).hasSameHashCodeAs(second);

    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(JobStatusType.class)
    @DisplayName("farkli tipteki durumlar birbirine esit degildir")
    void equals_returnsFalse_forDifferentTypes(final JobStatusType type) {

        final JobStatus status = JobStatus.of(type);

        for (final JobStatusType other : JobStatusType.values()) {
            if (other != type) {
                assertThat(status).isNotEqualTo(JobStatus.of(other));
            }
        }

    }

}
