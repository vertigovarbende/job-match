package com.deveyk.jobmatch.job.infrastructure.elasticsearch.adapter;

import com.deveyk.jobmatch.job.application.port.in.query.JobSearchCriteria;
import com.deveyk.jobmatch.job.domain.model.Job;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.JobDocument;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.filter.JobSearchDocumentFilter;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.mapper.JobDocumentMapper;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobSearchQueryAdapter {

    private final ElasticsearchOperations elasticsearchOperations;
    private final JobDocumentMapper jobDocumentMapper;

    public JmPage<Job> findAllPublished(final JobSearchCriteria criteria, final Pageable pageable) {

        final JobSearchDocumentFilter filter = JobSearchDocumentFilter.builder()
                .q(criteria.q())
                .seniority(criteria.seniority())
                .employmentType(criteria.employmentType())
                .workplaceType(criteria.workplaceType())
                .locationCountry(criteria.locationCountry())
                .locationCity(criteria.locationCity())
                .salaryMin(criteria.salaryMin())
                .skillIds(criteria.skillIds())
                .build();

        final Query query = filter.toQuery().setPageable(pageable);

        final SearchHits<JobDocument> searchHits = this.elasticsearchOperations.search(query, JobDocument.class);
        final SearchPage<JobDocument> searchPage = SearchHitSupport.searchPageFor(searchHits, pageable);

        final var content = searchPage.getSearchHits().getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(this.jobDocumentMapper::toDomain)
                .toList();

        log.debug("Elasticsearch job search: totalHits={}", searchHits.getTotalHits());

        return JmPage.of(filter, searchPage, content);

    }

}
