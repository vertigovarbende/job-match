package com.deveyk.jobmatch.job.infrastructure.persistence.repository;

import com.deveyk.jobmatch.job.infrastructure.persistence.entity.JobSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface SpringDataJobSkillJpaRepository extends JpaRepository<JobSkillEntity, Long> {

    List<JobSkillEntity> findAllByJobId(Long jobId);

    List<JobSkillEntity> findAllByJobIdIn(Collection<Long> jobIds);

    @Modifying
    @Query("delete from JobSkillEntity e where e.jobId = :jobId")
    void deleteAllByJobId(@Param("jobId") Long jobId);

}
