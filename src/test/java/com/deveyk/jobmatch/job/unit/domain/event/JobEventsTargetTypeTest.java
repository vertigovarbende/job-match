package com.deveyk.jobmatch.job.unit.domain.event;

import com.deveyk.jobmatch.job.domain.JobTargetType;
import com.deveyk.jobmatch.job.domain.event.JobArchivedEvent;
import com.deveyk.jobmatch.job.domain.event.JobClosedEvent;
import com.deveyk.jobmatch.job.domain.event.JobExpiredEvent;
import com.deveyk.jobmatch.job.domain.event.JobPublishedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Job Event'leri - targetType Testleri")
class JobEventsTargetTypeTest {

    @Test
    @DisplayName("JobTargetType.JOB audit_log'a yazilan sabit \"JOB\" degerini tasir")
    void jobTargetType_hasStableWireValue() {

        assertThat(JobTargetType.JOB).isEqualTo("JOB");

    }

    @Test
    @DisplayName("JobPublishedEvent targetType() olarak JobTargetType.JOB doner")
    void jobPublishedEvent_returnsJobTargetType() {

        final JobPublishedEvent event = new JobPublishedEvent("actor-1", "1");

        assertThat(event.targetType()).isEqualTo(JobTargetType.JOB);

    }

    @Test
    @DisplayName("JobClosedEvent targetType() olarak JobTargetType.JOB doner")
    void jobClosedEvent_returnsJobTargetType() {

        final JobClosedEvent event = new JobClosedEvent("actor-1", "1");

        assertThat(event.targetType()).isEqualTo(JobTargetType.JOB);

    }

    @Test
    @DisplayName("JobArchivedEvent targetType() olarak JobTargetType.JOB doner")
    void jobArchivedEvent_returnsJobTargetType() {

        final JobArchivedEvent event = new JobArchivedEvent("actor-1", "1");

        assertThat(event.targetType()).isEqualTo(JobTargetType.JOB);

    }

    @Test
    @DisplayName("JobExpiredEvent targetType() olarak JobTargetType.JOB doner")
    void jobExpiredEvent_returnsJobTargetType() {

        final JobExpiredEvent event = new JobExpiredEvent("1");

        assertThat(event.targetType()).isEqualTo(JobTargetType.JOB);

    }

}
