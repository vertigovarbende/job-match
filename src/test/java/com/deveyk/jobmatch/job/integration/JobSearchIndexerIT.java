package com.deveyk.jobmatch.job.integration;

import com.deveyk.jobmatch.company.infrastructure.persistence.entity.CompanyEntity;
import com.deveyk.jobmatch.job.domain.model.Job;
import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.JobDocument;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.adapter.JobSearchIndexer;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.repository.JobElasticsearchRepository;
import com.deveyk.jobmatch.job.infrastructure.persistence.adapter.JobRepositoryAdapter;
import com.deveyk.jobmatch.job.testsupport.JobTestDataBuilder;
import com.deveyk.jobmatch.testsupport.TestContainerConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JobSearchIndexer - Entegrasyon Testleri")
@Transactional
class JobSearchIndexerIT extends TestContainerConfiguration {

    @Autowired
    private JobSearchIndexer jobSearchIndexer;

    @Autowired
    private JobElasticsearchRepository jobElasticsearchRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private JobRepositoryAdapter jobRepositoryAdapter;

    @Autowired
    private TestEntityManager testEntityManager;

    private Long companyId;

    @BeforeEach
    void setUp() {

        this.jobElasticsearchRepository.deleteAll();

        final CompanyEntity company = this.testEntityManager.persistAndFlush(CompanyEntity.builder()
                .name("Test Company")
                .verified(false)
                .createdAt(LocalDateTime.now())
                .build()
        );

        this.companyId = company.getId();

    }

    @AfterEach
    void tearDown() {

        this.jobElasticsearchRepository.deleteAll();

    }

    @Test
    @DisplayName("removeAll() index'teki tum dokumanlari siler")
    void removeAll_deletesAllDocumentsFromIndex() {

        // given
        this.jobElasticsearchRepository.save(aDocument("9001"));
        this.jobElasticsearchRepository.save(aDocument("9002"));
        assertThat(this.jobElasticsearchRepository.count()).isEqualTo(2);

        // when
        this.jobSearchIndexer.removeAll();

        // then
        assertThat(this.jobElasticsearchRepository.count()).isZero();

    }

    @Test
    @DisplayName("removeAll() index'in kendisini ve mapping'ini silmez, yalnizca dokumanlari siler")
    void removeAll_keepsIndexAndMapping() {

        // given
        this.jobElasticsearchRepository.save(aDocument("9003"));

        // when
        this.jobSearchIndexer.removeAll();

        // then
        final IndexOperations indexOperations = this.elasticsearchOperations.indexOps(JobDocument.class);
        assertThat(indexOperations.exists()).isTrue();
        assertThat(indexOperations.getMapping()).isNotEmpty();

    }

    @Test
    @DisplayName("findIndexableTargetIds() yalnizca PUBLISHED job id'lerini String olarak doner")
    void findIndexableTargetIds_returnsOnlyPublishedJobIds() {

        // given
        final Job draft = this.jobRepositoryAdapter.save(this.aDraftJob());
        final Job published = this.savePublishedJob();

        // when
        final List<String> ids = this.jobSearchIndexer.findIndexableTargetIds(0, 1000);

        // then
        assertThat(ids)
                .contains(String.valueOf(published.getId()))
                .doesNotContain(String.valueOf(draft.getId()));

    }

    @Test
    @DisplayName("index() PUBLISHED bir job'u Postgres'ten okuyup index'e yazar")
    void index_writesPublishedJobFromPostgresToIndex() {

        // given
        final Job published = this.savePublishedJob();
        final String targetId = String.valueOf(published.getId());

        // when
        this.jobSearchIndexer.index(targetId);

        // then
        final Optional<JobDocument> document = this.jobElasticsearchRepository.findById(targetId);
        assertThat(document).isPresent();
        assertThat(document.get().getTitle()).isEqualTo(published.getTitle());

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

    private static JobDocument aDocument(final String id) {
        return JobDocument.builder()
                .id(id)
                .title("Backend Developer")
                .status(JobStatusType.PUBLISHED)
                .build();
    }

}
