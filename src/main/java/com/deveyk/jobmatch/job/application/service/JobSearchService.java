package com.deveyk.jobmatch.job.application.service;

import com.deveyk.jobmatch.job.application.port.in.JobSearchUseCase;
import com.deveyk.jobmatch.job.application.port.in.query.JobSearchCriteria;
import com.deveyk.jobmatch.job.application.port.in.query.JobSearchResult;
import com.deveyk.jobmatch.job.application.port.out.JobSearchPort;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobSearchService implements JobSearchUseCase {

    private final JobSearchPort jobSearchPort;

    @Override
    @Transactional(readOnly = true)
    public JmPage<JobSearchResult> searchPublishedJobs(final JobSearchCriteria criteria, final Pageable pageable) {

        log.debug("Searching published jobs");

        return this.jobSearchPort.search(criteria, pageable);
    }

}
