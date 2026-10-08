package com.deveyk.jobmatch.job.integration;

import com.deveyk.jobmatch.company.infrastructure.persistence.entity.CompanyEntity;
import com.deveyk.jobmatch.job.domain.model.Job;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.JobDocument;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.adapter.JobSearchIndexer;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.repository.JobElasticsearchRepository;
import com.deveyk.jobmatch.job.infrastructure.persistence.adapter.JobRepositoryAdapter;
import com.deveyk.jobmatch.job.testsupport.JobTestDataBuilder;
import com.deveyk.jobmatch.shared.domain.Seniority;
import com.deveyk.jobmatch.testsupport.TestContainerConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * GET /api/v1/jobs/search endpoint'inin HTTP sozlesmesini kilitleyen karakterizasyon testi
 * (bkz. REFACTOR_01 madde 3/4): arama use-case'i ve controller'i CRUD'dan ayrilirken JSON sekli
 * (duz job alanlari + highlights, sayfa zarfi) ve public erisim degismemelidir.
 */
@DisplayName("Job Search API - Entegrasyon Testleri")
@AutoConfigureMockMvc
@Transactional
class JobSearchApiIT extends TestContainerConfiguration {

    private static final String SEARCH_URL = "/api/v1/jobs/search";
    private static final String PAGING = "pageable.page=1&pageable.pageSize=20";

    @Autowired
    private MockMvc mockMvc;

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
    @DisplayName("GET /jobs/search token olmadan 200 doner ve job alanlarini duz, highlights'i ayni seviyede dondurur")
    void search_isPublicAndReturnsFlatJobJsonWithHighlights() throws Exception {

        // given
        final Job matching = this.indexPublishedJob("Zymurgist Engineer", Seniority.MID_SENIOR);
        this.indexPublishedJob("Marketing Manager", Seniority.MID_SENIOR);

        // when & then
        this.mockMvc.perform(get(SEARCH_URL + "?q=Zymurgist&" + PAGING))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response.totalElementCount").value(1))
                .andExpect(jsonPath("$.response.content.length()").value(1))
                .andExpect(jsonPath("$.response.content[0].id").value(matching.getId()))
                .andExpect(jsonPath("$.response.content[0].title").value("Zymurgist Engineer"))
                .andExpect(jsonPath("$.response.content[0].seniority").value("MID_SENIOR"))
                .andExpect(jsonPath("$.response.content[0].status").value("PUBLISHED"))
                .andExpect(jsonPath("$.response.content[0].highlights.title").isNotEmpty())
                .andExpect(jsonPath("$.response.content[0]", not(hasKey("job"))));

    }

    @Test
    @DisplayName("GET /jobs/search q verilmezse tum yayinlanmis ilanlari doner ve her sonucta highlights anahtari bulunur")
    void search_returnsAllPublishedJobsWithHighlightsKey_whenQueryIsNotGiven() throws Exception {

        // given
        this.indexPublishedJob("Alpha Engineer", Seniority.MID_SENIOR);
        this.indexPublishedJob("Beta Designer", Seniority.ENTRY_LEVEL);

        // when & then
        this.mockMvc.perform(get(SEARCH_URL + "?" + PAGING))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.totalElementCount").value(2))
                .andExpect(jsonPath("$.response.content.length()").value(2))
                .andExpect(jsonPath("$.response.content[0]", hasKey("highlights")))
                .andExpect(jsonPath("$.response.content[1]", hasKey("highlights")));

    }

    @Test
    @DisplayName("GET /jobs/search seniority filtresine uymayan ilanlari eler")
    void search_excludesJobsThatDoNotMatchSeniorityFilter() throws Exception {

        // given
        final Job midSenior = this.indexPublishedJob("Gamma Engineer", Seniority.MID_SENIOR);
        this.indexPublishedJob("Delta Engineer", Seniority.ENTRY_LEVEL);

        // when & then
        this.mockMvc.perform(get(SEARCH_URL + "?seniority=MID_SENIOR&" + PAGING))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content.length()").value(1))
                .andExpect(jsonPath("$.response.content[0].id").value(midSenior.getId()));

    }

    @Test
    @DisplayName("GET /jobs/search kabul edilmeyen bir sort alani verilirse 400 doner")
    void search_returns400_whenSortPropertyIsNotAccepted() throws Exception {

        this.mockMvc.perform(get(SEARCH_URL + "?" + PAGING
                        + "&pageable.orders[0].property=unknownProperty&pageable.orders[0].direction=ASC"))
                .andExpect(status().isBadRequest());

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
        this.elasticsearchOperations.indexOps(JobDocument.class).refresh();

        return published;

    }

}
