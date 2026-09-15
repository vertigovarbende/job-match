// SpringDataCandidateSkillJpaRepository.java
package com.deveyk.jobmatch.candidate.infrastructure.persistence.repository;

import com.deveyk.jobmatch.candidate.infrastructure.persistence.entity.CandidateSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataCandidateSkillJpaRepository extends JpaRepository<CandidateSkillEntity, Long> {

    Optional<CandidateSkillEntity> findByCandidateIdAndSkillId(Long candidateId, Long skillId);

    List<CandidateSkillEntity> findAllByCandidateId(Long candidateId);

    void deleteByCandidateIdAndSkillId(Long candidateId, Long skillId);

}