package com.deveyk.jobmatch.job.infrastructure.elasticsearch.repository;

import com.deveyk.jobmatch.job.infrastructure.elasticsearch.JobDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface JobElasticsearchRepository extends ElasticsearchRepository<JobDocument, String> {

}
