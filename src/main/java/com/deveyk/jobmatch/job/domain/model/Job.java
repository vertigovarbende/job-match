package com.deveyk.jobmatch.job.domain.model;

import com.deveyk.jobmatch.job.domain.exception.DuplicateSkillReferenceException;
import com.deveyk.jobmatch.job.domain.exception.InvalidJobStatusTransitionException;
import com.deveyk.jobmatch.job.domain.exception.JobFieldInvalidException;
import com.deveyk.jobmatch.job.domain.exception.JobNotReadyForPublishException;
import com.deveyk.jobmatch.shared.domain.EmploymentType;
import com.deveyk.jobmatch.shared.domain.Seniority;
import com.deveyk.jobmatch.shared.domain.WorkplaceType;
import com.deveyk.jobmatch.shared.domain.model.JmBaseDomain;
import com.deveyk.jobmatch.shared.domain.model.Location;
import com.deveyk.jobmatch.shared.domain.model.SalaryRange;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public final class Job extends JmBaseDomain {

    private final Long id;
    private final Long companyId;
    private String title;
    private String description;
    private Seniority seniority;
    private EmploymentType employmentType;
    private WorkplaceType workplaceType;
    private Location location;
    private SalaryRange salaryRange;
    private Integer minimumExperience;
    private LocalDateTime expiresAt;
    private LocalDateTime publishedAt;
    private JobStatus status;
    private Set<Long> requiredSkillIds;
    private Set<Long> preferredSkillIds;

    public static Job create(
            final Long companyId,
            final String title,
            final String description,
            final Seniority seniority,
            final EmploymentType employmentType,
            final WorkplaceType workplaceType,
            final Location location,
            final SalaryRange salaryRange,
            final Integer minimumExperience,
            final LocalDateTime expiresAt,
            final Set<Long> requiredSkillIds,
            final Set<Long> preferredSkillIds
    ) {

        validateTitle(title);

        final Set<Long> required = normalizeSkillIds(requiredSkillIds);
        final Set<Long> preferred = normalizeSkillIds(preferredSkillIds);
        assertNoOverlap(required, preferred);

        return Job.builder()
                .id(null)
                .companyId(Objects.requireNonNull(companyId, "companyId must not be null"))
                .title(title)
                .description(description)
                .seniority(seniority)
                .employmentType(employmentType)
                .workplaceType(workplaceType)
                .location(location)
                .salaryRange(salaryRange)
                .minimumExperience(minimumExperience)
                .expiresAt(expiresAt)
                .requiredSkillIds(required)
                .preferredSkillIds(preferred)
                .status(new DraftStatus())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void updateDetails(
            final String title,
            final String description,
            final Seniority seniority,
            final EmploymentType employmentType,
            final WorkplaceType workplaceType,
            final Location location,
            final SalaryRange salaryRange,
            final Integer minimumExperience,
            final LocalDateTime expiresAt,
            final Set<Long> requiredSkillIds,
            final Set<Long> preferredSkillIds
    ) {

        if (!this.status.canEdit()) {
            throw new InvalidJobStatusTransitionException(this.status.type(), "update");
        }

        validateTitle(title);

        final Set<Long> required = normalizeSkillIds(requiredSkillIds);
        final Set<Long> preferred = normalizeSkillIds(preferredSkillIds);
        assertNoOverlap(required, preferred);

        this.title = title;
        this.description = description;
        this.seniority = seniority;
        this.employmentType = employmentType;
        this.workplaceType = workplaceType;
        this.location = location;
        this.salaryRange = salaryRange;
        this.minimumExperience = minimumExperience;
        this.expiresAt = expiresAt;
        this.requiredSkillIds = required;
        this.preferredSkillIds = preferred;

    }

    public void publish() {

        if (this.location == null || this.salaryRange == null || this.employmentType == null || this.workplaceType == null) {
            throw new JobNotReadyForPublishException();
        }

        this.status = this.status.publish();
        this.publishedAt = LocalDateTime.now();

    }

    public void close() {
        this.status = this.status.close();
    }

    public void archive() {
        this.status = this.status.archive();
    }

    public void expire() {
        this.status = this.status.expire();
    }

    public boolean isOwnedBy(final Long companyId) {
        return this.companyId.equals(companyId);
    }

    private static void validateTitle(final String title) {
        if (title == null || title.isBlank()) {
            throw new JobFieldInvalidException("title");
        }
    }

    private static Set<Long> normalizeSkillIds(final Set<Long> skillIds) {
        return skillIds == null ? Set.of() : Set.copyOf(skillIds);
    }

    private static void assertNoOverlap(final Set<Long> requiredSkillIds, final Set<Long> preferredSkillIds) {

        final Set<Long> overlap = new HashSet<>(requiredSkillIds);
        overlap.retainAll(preferredSkillIds);

        if (!overlap.isEmpty()) {
            throw new DuplicateSkillReferenceException(overlap);
        }

    }

}
