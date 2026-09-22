package com.deveyk.jobmatch.job.application.port.out;

import com.deveyk.jobmatch.job.application.port.in.query.JobListCriteria;
import com.deveyk.jobmatch.job.application.port.in.query.JobSearchCriteria;
import com.deveyk.jobmatch.job.application.port.in.query.JobSearchResult;
import com.deveyk.jobmatch.job.domain.model.Job;
import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JobRepository {

    Optional<Job> findById(Long id);

    Job save(Job job);

    List<Job> findAllByStatusAndExpiresAtBefore(JobStatusType status, LocalDateTime cutoff);

    JmPage<JobSearchResult> findAllPublished(JobSearchCriteria criteria, Pageable pageable);

    JmPage<Job> findAllForCompany(Long companyId, JobListCriteria criteria, Pageable pageable);

}
