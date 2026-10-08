package com.deveyk.jobmatch.job.application.port.in;

import com.deveyk.jobmatch.job.application.port.in.query.JobSearchCriteria;
import com.deveyk.jobmatch.job.application.port.in.query.JobSearchResult;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import org.springframework.data.domain.Pageable;

public interface JobSearchUseCase {

    JmPage<JobSearchResult> searchPublishedJobs(JobSearchCriteria criteria, Pageable pageable);

}
