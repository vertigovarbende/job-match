package com.deveyk.jobmatch.job.unit.presentation.rest.mapper;

import com.deveyk.jobmatch.job.application.port.in.query.JobSearchResult;
import com.deveyk.jobmatch.job.domain.model.Job;
import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import com.deveyk.jobmatch.job.domain.model.PublishedStatus;
import com.deveyk.jobmatch.job.presentation.rest.mapper.JobResponseMapperImpl;
import com.deveyk.jobmatch.job.presentation.rest.mapper.JobSearchResponseMapper;
import com.deveyk.jobmatch.job.presentation.rest.mapper.JobSearchResponseMapperImpl;
import com.deveyk.jobmatch.job.presentation.rest.response.JobSearchHitResponse;
import com.deveyk.jobmatch.job.testsupport.JobTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link JobSearchResponseMapper} (MapStruct tarafindan uretilen {@link JobSearchResponseMapperImpl}) icin
 * birim testleri. Spring context'e ihtiyac duymaz: mapper constructor injection kullanir.
 */
@DisplayName("JobSearchResponseMapper - Birim Testleri")
class JobSearchResponseMapperTest {

    private final JobSearchResponseMapper mapper = new JobSearchResponseMapperImpl(new JobResponseMapperImpl());

    @Test
    @DisplayName("toResponse() job'u JobResponse'a cevirir ve highlights'i oldugu gibi tasir")
    void toResponse_mapsJobAndCarriesHighlights() {

        final Map<String, List<String>> highlights = Map.of("title", List.of("<em>Backend</em> Developer"));
        final JobSearchResult result = new JobSearchResult(aPublishedJob(7L, "Backend Developer"), highlights);

        final JobSearchHitResponse response = this.mapper.toResponse(result);

        assertThat(response.job().id()).isEqualTo(7L);
        assertThat(response.job().title()).isEqualTo("Backend Developer");
        assertThat(response.job().status()).isEqualTo(JobStatusType.PUBLISHED);
        assertThat(response.highlights()).isEqualTo(highlights);

    }

    @Test
    @DisplayName("toResponseList() sonuclarin sirasini ve her birinin highlights'ini korur")
    void toResponseList_preservesOrderAndHighlights() {

        final JobSearchResult first = new JobSearchResult(aPublishedJob(1L, "First"), Map.of("title", List.of("<em>First</em>")));
        final JobSearchResult second = new JobSearchResult(aPublishedJob(2L, "Second"), Map.of());

        final List<JobSearchHitResponse> responses = this.mapper.toResponseList(List.of(first, second));

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).job().id()).isEqualTo(1L);
        assertThat(responses.get(0).highlights()).containsKey("title");
        assertThat(responses.get(1).job().id()).isEqualTo(2L);
        assertThat(responses.get(1).highlights()).isEmpty();

    }

    private static Job aPublishedJob(final Long id, final String title) {
        return JobTestDataBuilder.aJob()
                .withId(id)
                .withTitle(title)
                .withStatus(new PublishedStatus())
                .buildPersisted();
    }

}
