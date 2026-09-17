package com.deveyk.jobmatch.job.application.port.out;

import com.deveyk.jobmatch.job.domain.model.Job;
import com.deveyk.jobmatch.job.domain.model.JobStatusType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JobRepository {

    Optional<Job> findById(Long id);

    Job save(Job job);

    List<Job> findAllByStatusAndExpiresAtBefore(JobStatusType status, LocalDateTime cutoff);

}
