package com.deveyk.jobmatch.job.infrastructure.persistence.adapter;

import com.deveyk.jobmatch.job.application.port.in.query.JobListCriteria;
import com.deveyk.jobmatch.job.application.port.in.query.JobSearchCriteria;
import com.deveyk.jobmatch.job.application.port.out.JobRepository;
import com.deveyk.jobmatch.job.domain.JobSkillType;
import com.deveyk.jobmatch.job.domain.exception.DuplicateSkillReferenceException;
import com.deveyk.jobmatch.job.domain.exception.JobFieldInvalidException;
import com.deveyk.jobmatch.job.domain.model.Job;
import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import com.deveyk.jobmatch.job.infrastructure.persistence.entity.JobEntity;
import com.deveyk.jobmatch.job.infrastructure.persistence.entity.JobSkillEntity;
import com.deveyk.jobmatch.job.infrastructure.persistence.filter.JobListFilter;
import com.deveyk.jobmatch.job.infrastructure.persistence.filter.JobSearchFilter;
import com.deveyk.jobmatch.job.infrastructure.persistence.mapper.JobPersistenceMapper;
import com.deveyk.jobmatch.job.infrastructure.persistence.repository.SpringDataJobJpaRepository;
import com.deveyk.jobmatch.job.infrastructure.persistence.repository.SpringDataJobSkillJpaRepository;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobRepositoryAdapter implements JobRepository {

    private static final String UNIQUE_CONSTRAINT_NAME = "uq_jm_job_skill_job_skill";
    private static final String SKILL_FK_CONSTRAINT_NAME = "fk_jm_job_skill_skill";

    private final SpringDataJobJpaRepository springDataJobJpaRepository;
    private final SpringDataJobSkillJpaRepository springDataJobSkillJpaRepository;
    private final JobPersistenceMapper jobPersistenceMapper;

    @Override
    public Optional<Job> findById(final Long id) {
        return this.springDataJobJpaRepository.findById(id)
                .map(this::toDomainWithSkills);
    }

    @Override
    public Job save(final Job job) {

        final JobEntity entity = this.jobPersistenceMapper.toEntity(job);
        final JobEntity saved = this.springDataJobJpaRepository.save(entity);

        this.replaceSkills(saved.getId(), job.getRequiredSkillIds(), job.getPreferredSkillIds());

        return this.toDomainWithSkills(saved);

    }

    @Override
    public List<Job> findAllByStatusAndExpiresAtBefore(final JobStatusType status, final LocalDateTime cutoff) {
        return this.springDataJobJpaRepository.findAllByStatusAndExpiresAtBefore(status, cutoff).stream()
                .map(this::toDomainWithSkills)
                .toList();
    }

    @Override
    public JmPage<Job> findAllPublished(final JobSearchCriteria criteria, final Pageable pageable) {

        final JobSearchFilter filter = JobSearchFilter.builder()
                .title(criteria.title())
                .seniority(criteria.seniority())
                .employmentType(criteria.employmentType())
                .workplaceType(criteria.workplaceType())
                .locationCountry(criteria.locationCountry())
                .locationCity(criteria.locationCity())
                .salaryMin(criteria.salaryMin())
                .skillIds(criteria.skillIds())
                .build();

        final Page<JobEntity> page = this.springDataJobJpaRepository.findAll(filter.toSpecification(), pageable);

        final var content = page.getContent().stream()
                .map(this::toDomainWithSkills)
                .toList();

        return JmPage.of(filter, page, content);

    }

    @Override
    public JmPage<Job> findAllForCompany(final Long companyId, final JobListCriteria criteria, final Pageable pageable) {

        final JobListFilter filter = JobListFilter.builder()
                .companyId(companyId)
                .status(criteria.status())
                .title(criteria.title())
                .seniority(criteria.seniority())
                .employmentType(criteria.employmentType())
                .workplaceType(criteria.workplaceType())
                .locationCountry(criteria.locationCountry())
                .locationCity(criteria.locationCity())
                .salaryMin(criteria.salaryMin())
                .skillIds(criteria.skillIds())
                .build();

        final Page<JobEntity> page = this.springDataJobJpaRepository.findAll(filter.toSpecification(), pageable);

        final var content = page.getContent().stream()
                .map(this::toDomainWithSkills)
                .toList();

        return JmPage.of(filter, page, content);

    }

    private Job toDomainWithSkills(final JobEntity entity) {

        final List<JobSkillEntity> skillEntities = this.springDataJobSkillJpaRepository.findAllByJobId(entity.getId());

        final Set<Long> requiredSkillIds = skillEntities.stream()
                .filter(skill -> skill.getSkillType() == JobSkillType.REQUIRED)
                .map(JobSkillEntity::getSkillId)
                .collect(Collectors.toSet());

        final Set<Long> preferredSkillIds = skillEntities.stream()
                .filter(skill -> skill.getSkillType() == JobSkillType.PREFERRED)
                .map(JobSkillEntity::getSkillId)
                .collect(Collectors.toSet());

        return this.jobPersistenceMapper.toDomain(entity, requiredSkillIds, preferredSkillIds);

    }

    private void replaceSkills(final Long jobId, final Set<Long> requiredSkillIds, final Set<Long> preferredSkillIds) {

        this.springDataJobSkillJpaRepository.deleteAllByJobId(jobId);

        final List<JobSkillEntity> rows = new ArrayList<>();

        requiredSkillIds.forEach(skillId -> rows.add(JobSkillEntity.builder()
                .jobId(jobId)
                .skillId(skillId)
                .skillType(JobSkillType.REQUIRED)
                .build()));

        preferredSkillIds.forEach(skillId -> rows.add(JobSkillEntity.builder()
                .jobId(jobId)
                .skillId(skillId)
                .skillType(JobSkillType.PREFERRED)
                .build()));

        if (rows.isEmpty()) {
            return;
        }

        try {

            this.springDataJobSkillJpaRepository.saveAll(rows);

        } catch (DataIntegrityViolationException violation) {

            final String constraintName = extractConstraintName(violation);

            if (UNIQUE_CONSTRAINT_NAME.equals(constraintName)) {

                log.warn("Duplicate jm_job_skill insert for jobId={}", jobId);
                throw new DuplicateSkillReferenceException(Set.copyOf(requiredSkillIds));

            }

            if (SKILL_FK_CONSTRAINT_NAME.equals(constraintName)) {

                log.warn("Invalid skillId referenced for jobId={}", jobId);
                throw new JobFieldInvalidException("skillId", "must reference an existing skill");

            }

            throw violation;

        }

    }

    private static String extractConstraintName(final DataIntegrityViolationException violation) {
        Throwable cause = violation.getCause();

        while (cause != null) {
            if (cause instanceof ConstraintViolationException constraintViolation) {
                return constraintViolation.getConstraintName();
            }
            cause = cause.getCause();
        }

        return null;
    }

}
