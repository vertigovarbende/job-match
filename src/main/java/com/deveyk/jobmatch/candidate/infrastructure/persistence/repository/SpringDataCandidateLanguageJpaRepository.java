// SpringDataCandidateLanguageJpaRepository.java
package com.deveyk.jobmatch.candidate.infrastructure.persistence.repository;

import com.deveyk.jobmatch.candidate.infrastructure.persistence.entity.CandidateLanguageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataCandidateLanguageJpaRepository extends JpaRepository<CandidateLanguageEntity, Long> {

    Optional<CandidateLanguageEntity> findByCandidateIdAndLanguageId(Long candidateId, Long languageId);

    List<CandidateLanguageEntity> findAllByCandidateId(Long candidateId);

    void deleteByCandidateIdAndLanguageId(Long candidateId, Long languageId);

}