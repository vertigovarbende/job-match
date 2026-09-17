package com.deveyk.jobmatch.candidate.presentation.rest.response;

import com.deveyk.jobmatch.candidate.domain.ProficiencyLevel;

public record CandidateLanguageResponse(
        Long id,
        Long candidateId,
        Long languageId,
        ProficiencyLevel proficiencyLevel
) {

}
