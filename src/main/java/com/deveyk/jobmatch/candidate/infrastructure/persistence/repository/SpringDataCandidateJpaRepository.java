package com.deveyk.jobmatch.candidate.infrastructure.persistence.repository;

import com.deveyk.jobmatch.candidate.infrastructure.persistence.entity.CandidateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataCandidateJpaRepository extends JpaRepository<CandidateEntity, Long> {

    Optional<CandidateEntity> findByUserId(Long userId);

}