package com.deveyk.jobmatch.job.infrastructure.persistence.adapter;

import com.deveyk.jobmatch.job.application.port.in.query.JobListCriteria;
import com.deveyk.jobmatch.job.application.port.in.query.JobSearchCriteria;
import com.deveyk.jobmatch.job.application.port.in.query.JobSearchResult;
import com.deveyk.jobmatch.job.application.port.out.JobRepository;
import com.deveyk.jobmatch.job.domain.JobSkillType;
import com.deveyk.jobmatch.job.domain.exception.DuplicateSkillReferenceException;
import com.deveyk.jobmatch.job.domain.exception.JobFieldInvalidException;
import com.deveyk.jobmatch.job.domain.model.Job;
import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.adapter.JobSearchQueryAdapter;
import com.deveyk.jobmatch.job.infrastructure.persistence.entity.JobEntity;
import com.deveyk.jobmatch.job.infrastructure.persistence.entity.JobSkillEntity;
import com.deveyk.jobmatch.job.infrastructure.persistence.filter.JobListFilter;
import com.deveyk.jobmatch.job.infrastructure.persistence.mapper.JobPersistenceMapper;
import com.deveyk.jobmatch.job.infrastructure.persistence.repository.SpringDataJobJpaRepository;
import com.deveyk.jobmatch.job.infrastructure.persistence.repository.SpringDataJobSkillJpaRepository;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import com.deveyk.jobmatch.shared.infrastructure.persistence.JmConstraintViolations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final JobSearchQueryAdapter jobSearchQueryAdapter;

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
        return this.toDomainListWithSkills(this.springDataJobJpaRepository.findAllByStatusAndExpiresAtBefore(status, cutoff));
    }

    @Override
    public JmPage<JobSearchResult> findAllPublished(final JobSearchCriteria criteria, final Pageable pageable) {
        return this.jobSearchQueryAdapter.findAllPublished(criteria, pageable);
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

        final List<Job> content = this.toDomainListWithSkills(page.getContent());

        return JmPage.of(filter, page, content);

    }

    private Job toDomainWithSkills(final JobEntity entity) {
        final List<JobSkillEntity> skillEntities = this.springDataJobSkillJpaRepository.findAllByJobId(entity.getId());
        return this.toDomain(entity, skillEntities);
    }

    private List<Job> toDomainListWithSkills(final List<JobEntity> entities) {

        if (entities.isEmpty()) {
            return List.of();
        }

        final List<Long> jobIds = entities.stream().map(JobEntity::getId).toList();

        final Map<Long, List<JobSkillEntity>> skillsByJobId = this.springDataJobSkillJpaRepository.findAllByJobIdIn(jobIds).stream()
                .collect(Collectors.groupingBy(JobSkillEntity::getJobId));

        return entities.stream()
                .map(entity -> this.toDomain(entity, skillsByJobId.getOrDefault(entity.getId(), List.of())))
                .toList();

    }

    private Job toDomain(final JobEntity entity, final List<JobSkillEntity> skillEntities) {

        final Set<Long> requiredSkillIds = filterSkillIds(skillEntities, JobSkillType.REQUIRED);
        final Set<Long> preferredSkillIds = filterSkillIds(skillEntities, JobSkillType.PREFERRED);

        return this.jobPersistenceMapper.toDomain(entity, requiredSkillIds, preferredSkillIds);

    }

    private static Set<Long> filterSkillIds(final List<JobSkillEntity> skillEntities, final JobSkillType type) {
        return skillEntities.stream()
                .filter(skill -> skill.getSkillType() == type)
                .map(JobSkillEntity::getSkillId)
                .collect(Collectors.toSet());
    }

    private void replaceSkills(final Long jobId, final Set<Long> requiredSkillIds, final Set<Long> preferredSkillIds) {

        final List<JobSkillEntity> existingRows = this.springDataJobSkillJpaRepository.findAllByJobId(jobId);
        final Set<Long> existingRequiredSkillIds = filterSkillIds(existingRows, JobSkillType.REQUIRED);
        final Set<Long> existingPreferredSkillIds = filterSkillIds(existingRows, JobSkillType.PREFERRED);

        if (existingRequiredSkillIds.equals(requiredSkillIds) && existingPreferredSkillIds.equals(preferredSkillIds)) {
            return;
        }

        this.springDataJobSkillJpaRepository.deleteAllByJobId(jobId);

        final List<JobSkillEntity> rows = new ArrayList<>();
        final LocalDateTime now = LocalDateTime.now();

        requiredSkillIds.forEach(skillId -> rows.add(JobSkillEntity.builder()
                .jobId(jobId)
                .skillId(skillId)
                .skillType(JobSkillType.REQUIRED)
                .createdAt(now)
                .build()));

        preferredSkillIds.forEach(skillId -> rows.add(JobSkillEntity.builder()
                .jobId(jobId)
                .skillId(skillId)
                .skillType(JobSkillType.PREFERRED)
                .createdAt(now)
                .build()));

        if (rows.isEmpty()) {
            return;
        }

        try {

            this.springDataJobSkillJpaRepository.saveAll(rows);

        } catch (DataIntegrityViolationException violation) {

            final String constraintName = JmConstraintViolations.extractConstraintName(violation);

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

}
