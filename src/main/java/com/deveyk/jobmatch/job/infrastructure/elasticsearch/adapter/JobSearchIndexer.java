package com.deveyk.jobmatch.job.infrastructure.elasticsearch.adapter;

import com.deveyk.jobmatch.job.application.port.out.JobRepository;
import com.deveyk.jobmatch.job.domain.exception.JobNotFoundException;
import com.deveyk.jobmatch.job.domain.model.Job;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.JobDocument;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.mapper.JobDocumentMapper;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.repository.JobElasticsearchRepository;
import com.deveyk.jobmatch.search.domain.indexer.SearchIndexer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobSearchIndexer implements SearchIndexer<JobDocument> {

    private final JobRepository jobRepository;
    private final JobElasticsearchRepository jobElasticsearchRepository;
    private final JobDocumentMapper jobDocumentMapper;

    @Override
    public String targetType() {
        return "JOB";
    }

    @Override
    public JobDocument toDocument(final String targetId) {

        final Long id = Long.valueOf(targetId);
        final Job job = this.jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException(id));

        return this.jobDocumentMapper.toDocument(job);
    }

    @Override
    public void indexDocument(final JobDocument document) {
        this.jobElasticsearchRepository.save(document);
        log.debug("Job indexed in Elasticsearch: id={}", document.getId());
    }

    @Override
    public void remove(final String targetId) {
        this.jobElasticsearchRepository.deleteById(targetId);
        log.debug("Job removed from Elasticsearch: id={}", targetId);
    }

}
