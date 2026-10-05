package com.deveyk.jobmatch.job.testsupport;

import com.deveyk.jobmatch.job.domain.model.DraftStatus;
import com.deveyk.jobmatch.job.domain.model.Job;
import com.deveyk.jobmatch.job.domain.model.JobStatus;
import com.deveyk.jobmatch.shared.domain.EmploymentType;
import com.deveyk.jobmatch.shared.domain.Seniority;
import com.deveyk.jobmatch.shared.domain.WorkplaceType;
import com.deveyk.jobmatch.shared.domain.model.Location;
import com.deveyk.jobmatch.shared.domain.model.Money;
import com.deveyk.jobmatch.shared.domain.model.SalaryRange;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public final class JobTestDataBuilder {

    private Long companyId = 1L;
    private String title = "Backend Developer";
    private String description = "description";
    private Seniority seniority = Seniority.MID_SENIOR;
    private EmploymentType employmentType = EmploymentType.FULL_TIME;
    private WorkplaceType workplaceType = WorkplaceType.REMOTE;
    private Location location = new Location("Türkiye", "İstanbul");
    private SalaryRange salaryRange = new SalaryRange(new Money(BigDecimal.valueOf(30000), "USD"), new Money(BigDecimal.valueOf(50000), "USD"));
    private Integer minimumExperience = 3;
    private LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);
    private Set<Long> requiredSkillIds = Set.of();
    private Set<Long> preferredSkillIds = Set.of();
    private Long id = null;
    private JobStatus status = new DraftStatus();
    private LocalDateTime createdAt = LocalDateTime.now();

    private JobTestDataBuilder() {

    }

    public static JobTestDataBuilder aJob() {
        return new JobTestDataBuilder();
    }

    public JobTestDataBuilder withCompanyId(final Long companyId) {
        this.companyId = companyId;
        return this;
    }

    public JobTestDataBuilder withTitle(final String title) {
        this.title = title;
        return this;
    }

    public JobTestDataBuilder withDescription(final String description) {
        this.description = description;
        return this;
    }

    public JobTestDataBuilder withSeniority(final Seniority seniority) {
        this.seniority = seniority;
        return this;
    }

    public JobTestDataBuilder withEmploymentType(final EmploymentType employmentType) {
        this.employmentType = employmentType;
        return this;
    }

    public JobTestDataBuilder withWorkplaceType(final WorkplaceType workplaceType) {
        this.workplaceType = workplaceType;
        return this;
    }

    public JobTestDataBuilder withLocation(final Location location) {
        this.location = location;
        return this;
    }

    public JobTestDataBuilder withSalaryRange(final SalaryRange salaryRange) {
        this.salaryRange = salaryRange;
        return this;
    }

    public JobTestDataBuilder withMinimumExperience(final Integer minimumExperience) {
        this.minimumExperience = minimumExperience;
        return this;
    }

    public JobTestDataBuilder withExpiresAt(final LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }

    public JobTestDataBuilder withRequiredSkillIds(final Set<Long> requiredSkillIds) {
        this.requiredSkillIds = requiredSkillIds;
        return this;
    }

    public JobTestDataBuilder withPreferredSkillIds(final Set<Long> preferredSkillIds) {
        this.preferredSkillIds = preferredSkillIds;
        return this;
    }

    public JobTestDataBuilder withId(final Long id) {
        this.id = id;
        return this;
    }

    public JobTestDataBuilder withStatus(final JobStatus status) {
        this.status = status;
        return this;
    }

    public JobTestDataBuilder withCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Job build() {
        return Job.create(
                this.companyId,
                this.title,
                this.description,
                this.seniority,
                this.employmentType,
                this.workplaceType,
                this.location,
                this.salaryRange,
                this.minimumExperience,
                this.expiresAt,
                this.requiredSkillIds,
                this.preferredSkillIds
        );
    }

    /**
     * Veritabanindan okunmus (rehydrate edilmis) bir Job uretir: Job.create() dogrulamalarini
     * ve normalizasyonunu atlar, id/status/createdAt degerlerini oldugu gibi kullanir.
     * Yeni Job (id=null, DRAFT) icin build() kullanilmalidir.
     */
    public Job buildPersisted() {
        return Job.builder()
                .id(this.id)
                .companyId(this.companyId)
                .title(this.title)
                .description(this.description)
                .seniority(this.seniority)
                .employmentType(this.employmentType)
                .workplaceType(this.workplaceType)
                .location(this.location)
                .salaryRange(this.salaryRange)
                .minimumExperience(this.minimumExperience)
                .expiresAt(this.expiresAt)
                .requiredSkillIds(this.requiredSkillIds)
                .preferredSkillIds(this.preferredSkillIds)
                .status(this.status)
                .createdAt(this.createdAt)
                .build();
    }

}
