package com.deveyk.jobmatch.candidate.application.port.out;

import com.deveyk.jobmatch.candidate.domain.model.Experience;

import java.util.List;
import java.util.Optional;

public interface ExperienceRepository {

    Optional<Experience> findById(Long id);

    List<Experience> findAllByCandidateId(Long candidateId);

    Experience save(Experience experience);

    void deleteById(Long id);

}