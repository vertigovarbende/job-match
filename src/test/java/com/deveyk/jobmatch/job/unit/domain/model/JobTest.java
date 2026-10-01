package com.deveyk.jobmatch.job.unit.domain.model;

import com.deveyk.jobmatch.job.domain.exception.DuplicateSkillReferenceException;
import com.deveyk.jobmatch.job.domain.exception.InvalidJobStatusTransitionException;
import com.deveyk.jobmatch.job.domain.exception.JobFieldInvalidException;
import com.deveyk.jobmatch.job.domain.exception.JobNotReadyForPublishException;
import com.deveyk.jobmatch.job.domain.model.Job;
import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import com.deveyk.jobmatch.job.testsupport.JobTestDataBuilder;
import com.deveyk.jobmatch.shared.domain.EmploymentType;
import com.deveyk.jobmatch.shared.domain.Seniority;
import com.deveyk.jobmatch.shared.domain.WorkplaceType;
import com.deveyk.jobmatch.shared.domain.model.Location;
import com.deveyk.jobmatch.shared.domain.model.Money;
import com.deveyk.jobmatch.shared.domain.model.SalaryRange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Job - Birim Testleri")
class JobTest {

    private static final Location VALID_LOCATION = new Location("Turkiye", "Istanbul");
    private static final SalaryRange VALID_SALARY_RANGE = new SalaryRange(
            new Money(BigDecimal.valueOf(50000), "USD"),
            new Money(BigDecimal.valueOf(80000), "USD"));

    @Test
    @DisplayName("create() tum alanlari set eder, status'u DRAFT yapar ve id'yi null birakir")
    void create_setsAllFieldsAndDefaultsToDraftStatus() {

        final LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);

        final Job job = JobTestDataBuilder.aJob()
                .withCompanyId(1L)
                .withTitle("Backend Developer")
                .withDescription("Job description")
                .withSeniority(Seniority.MID_SENIOR)
                .withEmploymentType(EmploymentType.FULL_TIME)
                .withWorkplaceType(WorkplaceType.REMOTE)
                .withLocation(VALID_LOCATION)
                .withSalaryRange(VALID_SALARY_RANGE)
                .withMinimumExperience(3)
                .withExpiresAt(expiresAt)
                .withRequiredSkillIds(Set.of(1L, 2L))
                .withPreferredSkillIds(Set.of(3L, 4L))
                .build();

        assertThat(job.getId()).isNull();
        assertThat(job.getCompanyId()).isEqualTo(1L);
        assertThat(job.getTitle()).isEqualTo("Backend Developer");
        assertThat(job.getDescription()).isEqualTo("Job description");
        assertThat(job.getSeniority()).isEqualTo(Seniority.MID_SENIOR);
        assertThat(job.getEmploymentType()).isEqualTo(EmploymentType.FULL_TIME);
        assertThat(job.getWorkplaceType()).isEqualTo(WorkplaceType.REMOTE);
        assertThat(job.getLocation()).isEqualTo(VALID_LOCATION);
        assertThat(job.getSalaryRange()).isEqualTo(VALID_SALARY_RANGE);
        assertThat(job.getMinimumExperience()).isEqualTo(3);
        assertThat(job.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(job.getRequiredSkillIds()).containsExactlyInAnyOrder(1L, 2L);
        assertThat(job.getPreferredSkillIds()).containsExactlyInAnyOrder(3L, 4L);
        assertThat(job.getStatus().type()).isEqualTo(JobStatusType.DRAFT);
        assertThat(job.getCreatedAt()).isNotNull();
        assertThat(job.getPublishedAt()).isNull();

    }

    @Test
    @DisplayName("create() title null oldugunda JobFieldInvalidException firlatir")
    void create_throwsJobFieldInvalidException_whenTitleIsNull() {

        assertThatThrownBy(() -> createJob(null, VALID_LOCATION, VALID_SALARY_RANGE, EmploymentType.FULL_TIME, WorkplaceType.REMOTE))
                .isInstanceOf(JobFieldInvalidException.class)
                .hasMessageContaining("title");

    }

    @Test
    @DisplayName("create() title bos (blank) oldugunda JobFieldInvalidException firlatir")
    void create_throwsJobFieldInvalidException_whenTitleIsBlank() {

        assertThatThrownBy(() -> createJob("   ", VALID_LOCATION, VALID_SALARY_RANGE, EmploymentType.FULL_TIME, WorkplaceType.REMOTE))
                .isInstanceOf(JobFieldInvalidException.class)
                .hasMessageContaining("title");

    }

    @Test
    @DisplayName("create() companyId null oldugunda NullPointerException firlatir")
    void create_throwsNullPointerException_whenCompanyIdIsNull() {

        assertThatThrownBy(() -> JobTestDataBuilder.aJob()
                .withCompanyId(null)
                .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("companyId");

    }

    @Test
    @DisplayName("create() null skill id setlerini bos sete normalize eder")
    void create_normalizesNullSkillIdsToEmptySets() {

        final Job job = JobTestDataBuilder.aJob()
                .withRequiredSkillIds(null)
                .withPreferredSkillIds(null)
                .build();

        assertThat(job.getRequiredSkillIds()).isEmpty();
        assertThat(job.getPreferredSkillIds()).isEmpty();

    }

    @Test
    @DisplayName("create() required ve preferred skill id'leri kesistiginde DuplicateSkillReferenceException firlatir")
    void create_throwsDuplicateSkillReferenceException_whenSkillIdsOverlap() {

        assertThatThrownBy(() -> JobTestDataBuilder.aJob()
                .withRequiredSkillIds(Set.of(1L, 2L))
                .withPreferredSkillIds(Set.of(2L, 3L))
                .build())
                .isInstanceOf(DuplicateSkillReferenceException.class)
                .hasMessageContaining("2");

    }

    @Test
    @DisplayName("updateDetails() DRAFT durumundaki job'in alanlarini gunceller")
    void updateDetails_updatesFieldsWhenStatusAllowsEdit() {

        final Job job = jobReadyToPublish();
        final LocalDateTime newExpiresAt = LocalDateTime.now().plusDays(60);

        job.updateDetails(
                "Senior Backend Developer",
                "Updated description",
                Seniority.DIRECTOR,
                EmploymentType.CONTRACT,
                WorkplaceType.HYBRID,
                new Location("Almanya", "Berlin"),
                new SalaryRange(new Money(BigDecimal.valueOf(90000), "EUR"), new Money(BigDecimal.valueOf(120000), "EUR")),
                5,
                newExpiresAt,
                Set.of(5L),
                Set.of(6L)
        );

        assertThat(job.getTitle()).isEqualTo("Senior Backend Developer");
        assertThat(job.getDescription()).isEqualTo("Updated description");
        assertThat(job.getSeniority()).isEqualTo(Seniority.DIRECTOR);
        assertThat(job.getEmploymentType()).isEqualTo(EmploymentType.CONTRACT);
        assertThat(job.getWorkplaceType()).isEqualTo(WorkplaceType.HYBRID);
        assertThat(job.getLocation()).isEqualTo(new Location("Almanya", "Berlin"));
        assertThat(job.getMinimumExperience()).isEqualTo(5);
        assertThat(job.getExpiresAt()).isEqualTo(newExpiresAt);
        assertThat(job.getRequiredSkillIds()).containsExactly(5L);
        assertThat(job.getPreferredSkillIds()).containsExactly(6L);

    }

    @Test
    @DisplayName("updateDetails() duzenlemeye kapali (PUBLISHED) bir job'da InvalidJobStatusTransitionException firlatir")
    void updateDetails_throwsInvalidJobStatusTransitionException_whenStatusDoesNotAllowEdit() {

        final Job job = jobReadyToPublish();
        job.publish();

        assertThatThrownBy(() -> job.updateDetails(
                "New Title",
                "New description",
                Seniority.DIRECTOR,
                EmploymentType.CONTRACT,
                WorkplaceType.HYBRID,
                VALID_LOCATION,
                VALID_SALARY_RANGE,
                5,
                LocalDateTime.now().plusDays(60),
                Set.of(),
                Set.of()
        ))
                .isInstanceOf(InvalidJobStatusTransitionException.class);

    }

    @Test
    @DisplayName("updateDetails() title bos oldugunda JobFieldInvalidException firlatir")
    void updateDetails_throwsJobFieldInvalidException_whenTitleIsBlank() {

        final Job job = jobReadyToPublish();

        assertThatThrownBy(() -> job.updateDetails(
                "   ",
                "description",
                Seniority.DIRECTOR,
                EmploymentType.CONTRACT,
                WorkplaceType.HYBRID,
                VALID_LOCATION,
                VALID_SALARY_RANGE,
                5,
                LocalDateTime.now().plusDays(60),
                Set.of(),
                Set.of()
        ))
                .isInstanceOf(JobFieldInvalidException.class)
                .hasMessageContaining("title");

    }

    @Test
    @DisplayName("updateDetails() skill id'leri kesistiginde DuplicateSkillReferenceException firlatir")
    void updateDetails_throwsDuplicateSkillReferenceException_whenSkillIdsOverlap() {

        final Job job = jobReadyToPublish();

        assertThatThrownBy(() -> job.updateDetails(
                "Backend Developer",
                "description",
                Seniority.DIRECTOR,
                EmploymentType.CONTRACT,
                WorkplaceType.HYBRID,
                VALID_LOCATION,
                VALID_SALARY_RANGE,
                5,
                LocalDateTime.now().plusDays(60),
                Set.of(7L),
                Set.of(7L)
        ))
                .isInstanceOf(DuplicateSkillReferenceException.class);

    }

    @Test
    @DisplayName("publish() gerekli tum alanlar doluyken DRAFT'tan PUBLISHED'e gecer ve publishedAt'i set eder")
    void publish_transitionsDraftToPublished_andSetsPublishedAt() {

        final Job job = jobReadyToPublish();

        job.publish();

        assertThat(job.getStatus().type()).isEqualTo(JobStatusType.PUBLISHED);
        assertThat(job.getPublishedAt()).isNotNull();

    }

    @Test
    @DisplayName("publish() location eksikken JobNotReadyForPublishException firlatir")
    void publish_throwsJobNotReadyForPublishException_whenLocationIsMissing() {

        final Job job = createJob("Backend Developer", null, VALID_SALARY_RANGE, EmploymentType.FULL_TIME, WorkplaceType.REMOTE);

        assertThatThrownBy(job::publish).isInstanceOf(JobNotReadyForPublishException.class);

    }

    @Test
    @DisplayName("publish() salaryRange eksikken JobNotReadyForPublishException firlatir")
    void publish_throwsJobNotReadyForPublishException_whenSalaryRangeIsMissing() {

        final Job job = createJob("Backend Developer", VALID_LOCATION, null, EmploymentType.FULL_TIME, WorkplaceType.REMOTE);

        assertThatThrownBy(job::publish).isInstanceOf(JobNotReadyForPublishException.class);

    }

    @Test
    @DisplayName("publish() employmentType eksikken JobNotReadyForPublishException firlatir")
    void publish_throwsJobNotReadyForPublishException_whenEmploymentTypeIsMissing() {

        final Job job = createJob("Backend Developer", VALID_LOCATION, VALID_SALARY_RANGE, null, WorkplaceType.REMOTE);

        assertThatThrownBy(job::publish).isInstanceOf(JobNotReadyForPublishException.class);

    }

    @Test
    @DisplayName("publish() workplaceType eksikken JobNotReadyForPublishException firlatir")
    void publish_throwsJobNotReadyForPublishException_whenWorkplaceTypeIsMissing() {

        final Job job = createJob("Backend Developer", VALID_LOCATION, VALID_SALARY_RANGE, EmploymentType.FULL_TIME, null);

        assertThatThrownBy(job::publish).isInstanceOf(JobNotReadyForPublishException.class);

    }

    @Test
    @DisplayName("publish() zaten PUBLISHED olan bir job'da InvalidJobStatusTransitionException firlatir")
    void publish_throwsInvalidJobStatusTransitionException_whenAlreadyPublished() {

        final Job job = jobReadyToPublish();
        job.publish();

        assertThatThrownBy(job::publish).isInstanceOf(InvalidJobStatusTransitionException.class);

    }

    @Test
    @DisplayName("isOwnedBy() companyId eslestiginde true doner")
    void isOwnedBy_returnsTrue_whenCompanyIdMatches() {

        final Job job = jobReadyToPublish();

        assertThat(job.isOwnedBy(1L)).isTrue();

    }

    @Test
    @DisplayName("isOwnedBy() companyId eslesmediginde false doner")
    void isOwnedBy_returnsFalse_whenCompanyIdDiffers() {

        final Job job = jobReadyToPublish();

        assertThat(job.isOwnedBy(2L)).isFalse();

    }

    private static Job jobReadyToPublish() {
        return createJob("Backend Developer", VALID_LOCATION, VALID_SALARY_RANGE, EmploymentType.FULL_TIME, WorkplaceType.REMOTE);
    }

    private static Job createJob(final String title,
                                  final Location location,
                                  final SalaryRange salaryRange,
                                  final EmploymentType employmentType,
                                  final WorkplaceType workplaceType) {
        return JobTestDataBuilder.aJob()
                .withTitle(title)
                .withDescription("Job description")
                .withEmploymentType(employmentType)
                .withWorkplaceType(workplaceType)
                .withLocation(location)
                .withSalaryRange(salaryRange)
                .withRequiredSkillIds(Set.of(1L, 2L))
                .withPreferredSkillIds(Set.of(3L, 4L))
                .build();
    }

}
