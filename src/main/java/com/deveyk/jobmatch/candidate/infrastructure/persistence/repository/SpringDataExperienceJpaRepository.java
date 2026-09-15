// SpringDataExperienceJpaRepository.java
package com.deveyk.jobmatch.candidate.infrastructure.persistence.repository;

import com.deveyk.jobmatch.candidate.infrastructure.persistence.entity.ExperienceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataExperienceJpaRepository extends JpaRepository<ExperienceEntity, Long> {

    List<ExperienceEntity> findAllByCandidateId(Long candidateId);

}