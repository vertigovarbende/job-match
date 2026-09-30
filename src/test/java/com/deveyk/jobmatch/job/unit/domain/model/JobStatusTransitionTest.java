package com.deveyk.jobmatch.job.unit.domain.model;

import com.deveyk.jobmatch.job.domain.exception.InvalidJobStatusTransitionException;
import com.deveyk.jobmatch.job.domain.model.JobStatus;
import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JobStatus - Durum Gecis Matrisi")
class JobStatusTransitionTest {

    @ParameterizedTest(name = "{0} -> {1}() => {2}")
    @MethodSource("allowedTransitions")
    @DisplayName("izin verilen gecis dogru durumu doner")
    void transition_movesToExpectedStatus(final JobStatusType from,
                                           final String action,
                                           final Function<JobStatus, JobStatus> transition,
                                           final JobStatusType expectedTo) {

        final JobStatus result = transition.apply(JobStatus.of(from));

        assertThat(result.type()).isEqualTo(expectedTo);

    }

    @ParameterizedTest(name = "{0} -> {1}() => InvalidJobStatusTransitionException")
    @MethodSource("forbiddenTransitions")
    @DisplayName("izin verilmeyen gecis InvalidJobStatusTransitionException firlatir")
    void transition_throwsInvalidJobStatusTransitionException(final JobStatusType from,
                                                                final String action,
                                                                final Function<JobStatus, JobStatus> transition) {

        assertThatThrownBy(() -> transition.apply(JobStatus.of(from)))
                .isInstanceOf(InvalidJobStatusTransitionException.class)
                .hasMessageContaining(from.toString())
                .hasMessageContaining(action);

    }

    @ParameterizedTest(name = "{0}.canEdit() => {1}")
    @MethodSource("canEditExpectations")
    @DisplayName("yalnizca DRAFT durumu duzenlemeye acik")
    void canEdit_reflectsWhetherStatusIsDraft(final JobStatusType type, final boolean expected) {

        assertThat(JobStatus.of(type).canEdit()).isEqualTo(expected);

    }

    static Stream<Arguments> allowedTransitions() {
        return Stream.of(
                Arguments.of(JobStatusType.DRAFT, "publish", (Function<JobStatus, JobStatus>) JobStatus::publish, JobStatusType.PUBLISHED),
                Arguments.of(JobStatusType.DRAFT, "archive", (Function<JobStatus, JobStatus>) JobStatus::archive, JobStatusType.ARCHIVED),
                Arguments.of(JobStatusType.PUBLISHED, "close", (Function<JobStatus, JobStatus>) JobStatus::close, JobStatusType.CLOSED),
                Arguments.of(JobStatusType.PUBLISHED, "expire", (Function<JobStatus, JobStatus>) JobStatus::expire, JobStatusType.EXPIRED),
                Arguments.of(JobStatusType.CLOSED, "archive", (Function<JobStatus, JobStatus>) JobStatus::archive, JobStatusType.ARCHIVED),
                Arguments.of(JobStatusType.EXPIRED, "archive", (Function<JobStatus, JobStatus>) JobStatus::archive, JobStatusType.ARCHIVED)
        );
    }

    static Stream<Arguments> forbiddenTransitions() {
        return Stream.of(
                Arguments.of(JobStatusType.DRAFT, "close", (Function<JobStatus, JobStatus>) JobStatus::close),
                Arguments.of(JobStatusType.DRAFT, "expire", (Function<JobStatus, JobStatus>) JobStatus::expire),
                Arguments.of(JobStatusType.PUBLISHED, "publish", (Function<JobStatus, JobStatus>) JobStatus::publish),
                Arguments.of(JobStatusType.PUBLISHED, "archive", (Function<JobStatus, JobStatus>) JobStatus::archive),
                Arguments.of(JobStatusType.CLOSED, "publish", (Function<JobStatus, JobStatus>) JobStatus::publish),
                Arguments.of(JobStatusType.CLOSED, "close", (Function<JobStatus, JobStatus>) JobStatus::close),
                Arguments.of(JobStatusType.CLOSED, "expire", (Function<JobStatus, JobStatus>) JobStatus::expire),
                Arguments.of(JobStatusType.EXPIRED, "publish", (Function<JobStatus, JobStatus>) JobStatus::publish),
                Arguments.of(JobStatusType.EXPIRED, "close", (Function<JobStatus, JobStatus>) JobStatus::close),
                Arguments.of(JobStatusType.EXPIRED, "expire", (Function<JobStatus, JobStatus>) JobStatus::expire),
                Arguments.of(JobStatusType.ARCHIVED, "publish", (Function<JobStatus, JobStatus>) JobStatus::publish),
                Arguments.of(JobStatusType.ARCHIVED, "close", (Function<JobStatus, JobStatus>) JobStatus::close),
                Arguments.of(JobStatusType.ARCHIVED, "archive", (Function<JobStatus, JobStatus>) JobStatus::archive),
                Arguments.of(JobStatusType.ARCHIVED, "expire", (Function<JobStatus, JobStatus>) JobStatus::expire)
        );
    }

    static Stream<Arguments> canEditExpectations() {
        return Stream.of(
                Arguments.of(JobStatusType.DRAFT, true),
                Arguments.of(JobStatusType.PUBLISHED, false),
                Arguments.of(JobStatusType.CLOSED, false),
                Arguments.of(JobStatusType.EXPIRED, false),
                Arguments.of(JobStatusType.ARCHIVED, false)
        );
    }

}
