package com.deveyk.jobmatch.candidate.application.port.out;

import com.deveyk.jobmatch.candidate.domain.model.CandidateLanguage;

import java.util.List;
import java.util.Optional;

public interface CandidateLanguageRepository {

    Optional<CandidateLanguage> findByCandidateIdAndLanguageId(Long candidateId, Long languageId);

    List<CandidateLanguage> findAllByCandidateId(Long candidateId);

    CandidateLanguage save(CandidateLanguage candidateLanguage);

    void deleteByCandidateIdAndLanguageId(Long candidateId, Long languageId);

}