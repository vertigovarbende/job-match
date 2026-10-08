package com.deveyk.jobmatch.job.application.port.out;

import com.deveyk.jobmatch.job.application.port.in.query.JobSearchCriteria;
import com.deveyk.jobmatch.job.application.port.in.query.JobSearchResult;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import org.springframework.data.domain.Pageable;

public interface JobSearchPort {

    JmPage<JobSearchResult> search(JobSearchCriteria criteria, Pageable pageable);

}
