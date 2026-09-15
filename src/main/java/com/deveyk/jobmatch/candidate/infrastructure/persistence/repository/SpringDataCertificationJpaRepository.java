// SpringDataCertificationJpaRepository.java
package com.deveyk.jobmatch.candidate.infrastructure.persistence.repository;

import com.deveyk.jobmatch.candidate.infrastructure.persistence.entity.CertificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataCertificationJpaRepository extends JpaRepository<CertificationEntity, Long> {

    List<CertificationEntity> findAllByCandidateId(Long candidateId);

}