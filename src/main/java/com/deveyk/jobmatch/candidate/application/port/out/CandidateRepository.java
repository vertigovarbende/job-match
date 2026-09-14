package com.deveyk.jobmatch.candidate.application.port.out;

import com.deveyk.jobmatch.candidate.domain.model.Candidate;

import java.util.Optional;

public interface CandidateRepository {

    Optional<Candidate> findByUserId(Long userId);

    Candidate save(Candidate candidate);

}