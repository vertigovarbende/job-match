package com.deveyk.jobmatch.search.integration;

import com.deveyk.jobmatch.company.infrastructure.persistence.entity.CompanyEntity;
import com.deveyk.jobmatch.job.domain.model.Job;
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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("SearchReindexController - Entegrasyon Testleri")
@AutoConfigureMockMvc
@Transactional
class SearchReindexControllerIT extends TestContainerConfiguration {

    private static final String REINDEX_JOB_URL = "/internal/search/reindex/job";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobElasticsearchRepository jobElasticsearchRepository;

    @Autowired
    private JobRepositoryAdapter jobRepositoryAdapter;

    @Autowired
    private TestEntityManager testEntityManager;

    private Long companyId;

    @BeforeEach
    void setUp() {

        // Elasticsearch transaction'a katilmaz: test baslamadan once index'i temizliyoruz
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

        // Postgres verisi @Transactional ile geri alinir, Elasticsearch verisi alinmaz
        this.jobElasticsearchRepository.deleteAll();

    }

    @Test
    @DisplayName("reindex kimlik dogrulamasi olmayan istege 401 doner")
    void reindex_returns401_whenRequestIsUnauthenticated() throws Exception {

        this.mockMvc.perform(post(REINDEX_JOB_URL))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("reindex CANDIDATE rolune 403 doner")
    void reindex_returns403_whenUserIsCandidate() throws Exception {

        this.mockMvc.perform(post(REINDEX_JOB_URL)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CANDIDATE"))))
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("reindex EMPLOYER rolune 403 doner")
    void reindex_returns403_whenUserIsEmployer() throws Exception {

        this.mockMvc.perform(post(REINDEX_JOB_URL)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLOYER"))))
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("reindex ADMIN icin PUBLISHED job'lari index'e yazar ve ozeti doner")
    void reindex_indexesPublishedJobsAndReturnsSummary_whenUserIsAdmin() throws Exception {

        // given
        final Job published = this.savePublishedJob();

        // when / then
        this.mockMvc.perform(post(REINDEX_JOB_URL)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response.targetType").value("JOB"))
                .andExpect(jsonPath("$.response.total").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.response.failed").value(0));

        assertThat(this.jobElasticsearchRepository.findById(String.valueOf(published.getId()))).isPresent();

    }

    @Test
    @DisplayName("reindex bilinmeyen targetType icin 404 ve SRC_001 hata kodunu doner")
    void reindex_returns404_whenTargetTypeIsUnknown() throws Exception {

        this.mockMvc.perform(post("/internal/search/reindex/unknown")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SRC_001"));

    }

    private Job savePublishedJob() {

        final Job saved = this.jobRepositoryAdapter.save(JobTestDataBuilder.aJob()
                .withCompanyId(this.companyId)
                .build());
        saved.publish();

        return this.jobRepositoryAdapter.save(saved);

    }

}
