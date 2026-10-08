package com.deveyk.jobmatch.job.unit.application.service;

import com.deveyk.jobmatch.job.application.port.in.query.JobSearchCriteria;
import com.deveyk.jobmatch.job.application.port.in.query.JobSearchResult;
import com.deveyk.jobmatch.job.application.port.out.JobSearchPort;
import com.deveyk.jobmatch.job.application.service.JobSearchService;
import com.deveyk.jobmatch.shared.domain.EmploymentType;
import com.deveyk.jobmatch.shared.domain.Seniority;
import com.deveyk.jobmatch.shared.domain.WorkplaceType;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JobSearchService - Birim Testleri")
class JobSearchServiceTest {

    @Mock
    private JobSearchPort jobSearchPort;

    @InjectMocks
    private JobSearchService jobSearchService;

    @Test
    @DisplayName("searchPublishedJobs() criteria ve pageable'i oldugu gibi search port'una iletir")
    void searchPublishedJobs_delegatesToSearchPortWithGivenCriteriaAndPageable() {

        final JobSearchCriteria criteria = JobSearchCriteria.builder()
                .q("backend")
                .seniority(Seniority.MID_SENIOR)
                .employmentType(EmploymentType.FULL_TIME)
                .workplaceType(WorkplaceType.REMOTE)
                .locationCountry("Turkiye")
                .locationCity("Istanbul")
                .salaryMin(BigDecimal.valueOf(50000))
                .skillIds(Set.of(1L))
                .build();

        final Pageable pageable = Pageable.ofSize(20);
        final JmPage<JobSearchResult> expected = JmPage.<JobSearchResult>builder().build();
        when(this.jobSearchPort.search(criteria, pageable)).thenReturn(expected);

        final JmPage<JobSearchResult> result = this.jobSearchService.searchPublishedJobs(criteria, pageable);

        assertThat(result).isSameAs(expected);
        verify(this.jobSearchPort).search(criteria, pageable);

    }

}
