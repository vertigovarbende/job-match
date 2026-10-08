package com.deveyk.jobmatch.job.integration;

import com.deveyk.jobmatch.company.infrastructure.persistence.entity.CompanyEntity;
import com.deveyk.jobmatch.job.application.port.in.query.JobSearchCriteria;
import com.deveyk.jobmatch.job.application.port.in.query.JobSearchResult;
import com.deveyk.jobmatch.job.domain.model.Job;
import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.JobDocument;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.adapter.JobSearchIndexer;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.adapter.JobSearchQueryAdapter;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.repository.JobElasticsearchRepository;
import com.deveyk.jobmatch.job.infrastructure.persistence.adapter.JobRepositoryAdapter;
import com.deveyk.jobmatch.job.testsupport.JobTestDataBuilder;
import com.deveyk.jobmatch.shared.domain.Seniority;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import com.deveyk.jobmatch.testsupport.TestContainerConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JobSearchQueryAdapter - Entegrasyon Testleri")
@Transactional
class JobSearchQueryAdapterIT extends TestContainerConfiguration {

    private static final Pageable PAGEABLE = PageRequest.of(0, 10);

    @Autowired
    private JobSearchQueryAdapter jobSearchQueryAdapter;

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
    @DisplayName("search() q ile eslesen yayinlanmis ilani domain Job'una cevirip highlight ile doner")
    void search_returnsMatchingPublishedJobWithHighlights_whenQueryIsGiven() {

        // given
        final Job matching = this.indexPublishedJob("Zymurgist Engineer", Seniority.MID_SENIOR);
        this.indexPublishedJob("Marketing Manager", Seniority.MID_SENIOR);

        // when
        final JmPage<JobSearchResult> page = this.jobSearchQueryAdapter.search(
                JobSearchCriteria.builder().q("Zymurgist").build(), PAGEABLE);

        // then
        assertThat(page.getContent()).hasSize(1);

        final JobSearchResult result = page.getContent().getFirst();
        assertThat(result.job().getId()).isEqualTo(matching.getId());
        assertThat(result.job().getTitle()).isEqualTo("Zymurgist Engineer");
        assertThat(result.highlights()).containsKey("title");
        assertThat(page.getTotalElementCount()).isEqualTo(1L);

    }

    @Test
    @DisplayName("search() q verilmezse tum yayinlanmis ilanlari doner ve highlight uretmez")
    void search_returnsAllPublishedJobsWithoutHighlights_whenQueryIsNotGiven() {

        // given
        this.indexPublishedJob("Alpha Engineer", Seniority.MID_SENIOR);
        this.indexPublishedJob("Beta Designer", Seniority.ENTRY_LEVEL);

        // when
        final JmPage<JobSearchResult> page = this.jobSearchQueryAdapter.search(
                JobSearchCriteria.builder().build(), PAGEABLE);

        // then
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getContent()).allSatisfy(result -> assertThat(result.highlights()).isEmpty());

    }

    @Test
    @DisplayName("search() seniority filtresine uymayan ilanlari eler")
    void search_excludesJobsThatDoNotMatchSeniorityFilter() {

        // given
        final Job midSenior = this.indexPublishedJob("Gamma Engineer", Seniority.MID_SENIOR);
        this.indexPublishedJob("Delta Engineer", Seniority.ENTRY_LEVEL);

        // when
        final JmPage<JobSearchResult> page = this.jobSearchQueryAdapter.search(
                JobSearchCriteria.builder().seniority(Seniority.MID_SENIOR).build(), PAGEABLE);

        // then
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().getFirst().job().getId()).isEqualTo(midSenior.getId());

    }

    @Test
    @DisplayName("search() index'e yanlislikla yazilmis PUBLISHED olmayan dokumanlari da eler")
    void search_excludesDocumentsThatAreNotPublished() {

        // given
        this.jobElasticsearchRepository.save(JobDocument.builder()
                .id("9001")
                .companyId(this.companyId)
                .title("Epsilon Engineer")
                .status(JobStatusType.DRAFT)
                .build());
        this.refreshIndex();

        // when
        final JmPage<JobSearchResult> page = this.jobSearchQueryAdapter.search(
                JobSearchCriteria.builder().q("Epsilon").build(), PAGEABLE);

        // then
        assertThat(page.getContent()).isEmpty();

    }

    private Job indexPublishedJob(final String title, final Seniority seniority) {

        final Job draft = this.jobRepositoryAdapter.save(JobTestDataBuilder.aJob()
                .withCompanyId(this.companyId)
                .withTitle(title)
                .withSeniority(seniority)
                .build());
        draft.publish();

        final Job published = this.jobRepositoryAdapter.save(draft);

        this.jobSearchIndexer.index(String.valueOf(published.getId()));
        this.refreshIndex();

        return published;

    }

    private void refreshIndex() {
        this.elasticsearchOperations.indexOps(JobDocument.class).refresh();
    }

}
