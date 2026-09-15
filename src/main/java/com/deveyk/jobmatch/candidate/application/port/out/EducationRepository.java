package com.deveyk.jobmatch.candidate.application.port.out;

import com.deveyk.jobmatch.candidate.domain.model.Education;

import java.util.List;
import java.util.Optional;

public interface EducationRepository {

    Optional<Education> findById(Long id);

    List<Education> findAllByCandidateId(Long candidateId);

    Education save(Education education);

    void deleteById(Long id);

}