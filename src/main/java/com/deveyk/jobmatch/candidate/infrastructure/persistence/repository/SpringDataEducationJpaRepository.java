// SpringDataEducationJpaRepository.java
package com.deveyk.jobmatch.candidate.infrastructure.persistence.repository;

import com.deveyk.jobmatch.candidate.infrastructure.persistence.entity.EducationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataEducationJpaRepository extends JpaRepository<EducationEntity, Long> {

    List<EducationEntity> findAllByCandidateId(Long candidateId);

}