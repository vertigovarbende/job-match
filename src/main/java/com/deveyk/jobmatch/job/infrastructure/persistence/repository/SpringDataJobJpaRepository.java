package com.deveyk.jobmatch.job.infrastructure.persistence.repository;

import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import com.deveyk.jobmatch.job.infrastructure.persistence.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SpringDataJobJpaRepository extends JpaRepository<JobEntity, Long> {

    List<JobEntity> findAllByStatusAndExpiresAtBefore(JobStatusType status, LocalDateTime cutoff);

}
