package com.deveyk.jobmatch.candidate.application.port.out;

import com.deveyk.jobmatch.candidate.domain.model.Certification;

import java.util.List;
import java.util.Optional;

public interface CertificationRepository {

    Optional<Certification> findById(Long id);

    List<Certification> findAllByCandidateId(Long candidateId);

    Certification save(Certification certification);

    void deleteById(Long id);

}