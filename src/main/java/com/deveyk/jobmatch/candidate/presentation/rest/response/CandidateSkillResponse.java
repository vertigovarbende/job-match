package com.deveyk.jobmatch.candidate.presentation.rest.response;

import com.deveyk.jobmatch.candidate.domain.ProficiencyLevel;

public record CandidateSkillResponse(
        Long id,
        Long candidateId,
        Long skillId,
        ProficiencyLevel proficiencyLevel
) {

}
