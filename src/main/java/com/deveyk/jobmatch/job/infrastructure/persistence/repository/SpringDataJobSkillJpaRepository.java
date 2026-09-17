package com.deveyk.jobmatch.job.infrastructure.persistence.repository;

import com.deveyk.jobmatch.job.infrastructure.persistence.entity.JobSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataJobSkillJpaRepository extends JpaRepository<JobSkillEntity, Long> {

    List<JobSkillEntity> findAllByJobId(Long jobId);

    void deleteAllByJobId(Long jobId);

}
