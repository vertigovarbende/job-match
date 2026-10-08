package com.deveyk.jobmatch.job.infrastructure.persistence.repository;

import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import com.deveyk.jobmatch.job.infrastructure.persistence.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SpringDataJobJpaRepository extends JpaRepository<JobEntity, Long>, JpaSpecificationExecutor<JobEntity> {

    List<JobEntity> findAllByStatusAndExpiresAtBefore(JobStatusType status, LocalDateTime cutoff);

    @Query("select j.id from JobEntity j where j.status = :status order by j.id asc")
    List<Long> findIdsByStatus(@Param("status") JobStatusType status, Pageable pageable);

}
