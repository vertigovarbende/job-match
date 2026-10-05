package com.deveyk.jobmatch.job.unit.infrastructure.elasticsearch.adapter;

import com.deveyk.jobmatch.job.application.port.out.JobRepository;
import com.deveyk.jobmatch.job.domain.JobTargetType;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.adapter.JobSearchIndexer;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.mapper.JobDocumentMapper;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.repository.JobElasticsearchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("JobSearchIndexer - Birim Testleri")
class JobSearchIndexerTest {

    @Test
    @DisplayName("targetType() JobTargetType.JOB doner")
    void targetType_returnsJobTargetType() {

        final JobSearchIndexer indexer = new JobSearchIndexer(
                mock(JobRepository.class),
                mock(JobElasticsearchRepository.class),
                mock(JobDocumentMapper.class));

        assertThat(indexer.targetType()).isEqualTo(JobTargetType.JOB);

    }

}
