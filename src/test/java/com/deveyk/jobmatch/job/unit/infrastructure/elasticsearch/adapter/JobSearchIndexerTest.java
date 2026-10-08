package com.deveyk.jobmatch.job.unit.infrastructure.elasticsearch.adapter;

import com.deveyk.jobmatch.job.application.port.out.JobRepository;
import com.deveyk.jobmatch.job.domain.JobTargetType;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.adapter.JobSearchIndexer;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.mapper.JobDocumentMapper;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.repository.JobElasticsearchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("JobSearchIndexer - Birim Testleri")
@ExtendWith(MockitoExtension.class)
class JobSearchIndexerTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobElasticsearchRepository jobElasticsearchRepository;

    @Mock
    private JobDocumentMapper jobDocumentMapper;

    @InjectMocks
    private JobSearchIndexer indexer;

    @Test
    @DisplayName("targetType() JobTargetType.JOB doner")
    void targetType_returnsJobTargetType() {

        assertThat(this.indexer.targetType()).isEqualTo(JobTargetType.JOB);

    }

    @Test
    @DisplayName("removeAll() Elasticsearch repository'sindeki tum dokumanlari siler")
    void removeAll_deletesAllDocuments() {

        this.indexer.removeAll();

        verify(this.jobElasticsearchRepository).deleteAll();

    }

    @Test
    @DisplayName("findIndexableTargetIds() PUBLISHED job id'lerini istenen sayfayla String olarak doner")
    void findIndexableTargetIds_returnsPublishedJobIdsAsStrings() {

        when(this.jobRepository.findPublishedJobIds(PageRequest.of(2, 50))).thenReturn(List.of(101L, 102L));

        final List<String> ids = this.indexer.findIndexableTargetIds(2, 50);

        assertThat(ids).containsExactly("101", "102");

    }

    @Test
    @DisplayName("findIndexableTargetIds() sayfa bos oldugunda bos liste doner")
    void findIndexableTargetIds_returnsEmptyList_whenPageIsEmpty() {

        when(this.jobRepository.findPublishedJobIds(PageRequest.of(5, 50))).thenReturn(List.of());

        final List<String> ids = this.indexer.findIndexableTargetIds(5, 50);

        assertThat(ids).isEmpty();

    }

}
