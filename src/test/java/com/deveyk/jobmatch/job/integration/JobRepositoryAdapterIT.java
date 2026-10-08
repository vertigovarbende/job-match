package com.deveyk.jobmatch.job.integration;

import com.deveyk.jobmatch.catalog.infrastructure.persistence.entity.SkillEntity;
import com.deveyk.jobmatch.catalog.infrastructure.persistence.repository.SpringDataSkillJpaRepository;
import com.deveyk.jobmatch.company.infrastructure.persistence.entity.CompanyEntity;
import com.deveyk.jobmatch.job.domain.model.Job;
import com.deveyk.jobmatch.job.infrastructure.persistence.adapter.JobRepositoryAdapter;
import com.deveyk.jobmatch.job.infrastructure.persistence.entity.JobSkillEntity;
import com.deveyk.jobmatch.job.infrastructure.persistence.repository.SpringDataJobSkillJpaRepository;
import com.deveyk.jobmatch.job.testsupport.JobTestDataBuilder;
import com.deveyk.jobmatch.testsupport.TestContainerConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JobRepositoryAdapter - Entegrasyon Testleri")
@Transactional
class JobRepositoryAdapterIT extends TestContainerConfiguration {

    @Autowired
    private JobRepositoryAdapter jobRepositoryAdapter;

    @Autowired
    private SpringDataJobSkillJpaRepository springDataJobSkillJpaRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private SpringDataSkillJpaRepository springDataSkillJpaRepository;

    private Long companyId;
    private Long skillIdA;
    private Long skillIdB;

    @BeforeEach
    void setUp() {

        final CompanyEntity company = this.testEntityManager.persistAndFlush(CompanyEntity.builder()
                .name("Test Company")
                .verified(false)
                .createdAt(LocalDateTime.now())
                .build()
        );

        this.companyId = company.getId();

        final List<SkillEntity> skills = this.springDataSkillJpaRepository.findAll();
        this.skillIdA = skills.get(0).getId();
        this.skillIdB = skills.get(1).getId();

    }

    @Test
    @DisplayName("save() skill setleri değişmeden (publish sonrası) tekrar çağrıldığında jm_job_skill satırları korunur (yeniden yazılmaz)")
    void save_preservesJobSkillRows_whenSkillsUnchangedAcrossStatusUpdate() {

        // given
        final Job job = JobTestDataBuilder.aJob()
                .withCompanyId(this.companyId)
                .withRequiredSkillIds(Set.of(this.skillIdA))
                .withPreferredSkillIds(Set.of(this.skillIdB))
                .build();

        final Job saved = this.jobRepositoryAdapter.save(job);
        final Set<Long> rowIdsBefore = this.jobSkillRowIds(saved.getId());

        // when
        saved.publish();
        this.jobRepositoryAdapter.save(saved);
        final Set<Long> rowIdsAfter = this.jobSkillRowIds(saved.getId());

        // then
        assertThat(rowIdsBefore).isNotEmpty();
        assertThat(rowIdsAfter).isEqualTo(rowIdsBefore);

    }

    @Test
    @DisplayName("save() skill seti değiştiğinde eski jm_job_skill satırları silinir, yeni satırlar doğru yazılır")
    void save_replacesJobSkillRows_whenSkillSetChanges() {

        // given
        final Job job = JobTestDataBuilder.aJob()
                .withCompanyId(this.companyId)
                .withRequiredSkillIds(Set.of(this.skillIdA))
                .withPreferredSkillIds(Set.of())
                .build();

        final Job saved = this.jobRepositoryAdapter.save(job);

        // when
        saved.updateDetails(
                saved.getTitle(),
                saved.getDescription(),
                saved.getSeniority(),
                saved.getEmploymentType(),
                saved.getWorkplaceType(),
                saved.getLocation(),
                saved.getSalaryRange(),
                saved.getMinimumExperience(),
                saved.getExpiresAt(),
                Set.of(),
                Set.of(this.skillIdB)
        );
        this.jobRepositoryAdapter.save(saved);

        // then
        final List<JobSkillEntity> rows = this.springDataJobSkillJpaRepository.findAllByJobId(saved.getId());
        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getSkillId()).isEqualTo(this.skillIdB);

    }

    @Test
    @DisplayName("findPublishedJobIds() yalnizca PUBLISHED job id'lerini id'ye gore artan sirada doner")
    void findPublishedJobIds_returnsOnlyPublishedJobIdsInAscendingOrder() {

        // given
        final Job draft = this.jobRepositoryAdapter.save(this.aDraftJob());
        final Job published = this.savePublishedJob();
        final Job closed = this.saveClosedJob();
        final Job anotherPublished = this.savePublishedJob();

        // when
        final List<Long> ids = this.jobRepositoryAdapter.findPublishedJobIds(PageRequest.of(0, 1000));

        // then
        assertThat(ids)
                .contains(published.getId(), anotherPublished.getId())
                .doesNotContain(draft.getId(), closed.getId())
                .isSorted();

    }

    @Test
    @DisplayName("findPublishedJobIds() sayfalar arasinda ayni sirayi korur ve kayit atlamaz")
    void findPublishedJobIds_pagesConsistentlyWithTheFullList() {

        // given
        this.savePublishedJob();
        this.savePublishedJob();

        final List<Long> all = this.jobRepositoryAdapter.findPublishedJobIds(PageRequest.of(0, 1000));

        // when
        final List<Long> firstPage = this.jobRepositoryAdapter.findPublishedJobIds(PageRequest.of(0, 1));
        final List<Long> secondPage = this.jobRepositoryAdapter.findPublishedJobIds(PageRequest.of(1, 1));

        // then
        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
        assertThat(firstPage).containsExactly(all.get(0));
        assertThat(secondPage).containsExactly(all.get(1));

    }

    private Job aDraftJob() {
        return JobTestDataBuilder.aJob()
                .withCompanyId(this.companyId)
                .build();
    }

    private Job savePublishedJob() {

        final Job saved = this.jobRepositoryAdapter.save(this.aDraftJob());
        saved.publish();

        return this.jobRepositoryAdapter.save(saved);

    }

    private Job saveClosedJob() {

        final Job published = this.savePublishedJob();
        published.close();

        return this.jobRepositoryAdapter.save(published);

    }

    private Set<Long> jobSkillRowIds(final Long jobId) {
        return this.springDataJobSkillJpaRepository.findAllByJobId(jobId).stream()
                .map(JobSkillEntity::getId)
                .collect(Collectors.toSet());
    }

}
