package com.deveyk.jobmatch.candidate.presentation.rest.request;

import com.deveyk.jobmatch.candidate.domain.ProficiencyLevel;
import jakarta.validation.constraints.NotNull;

public record AttachCandidateSkillRequest(
        @NotNull
        Long skillId,

        @NotNull
        ProficiencyLevel proficiencyLevel
) {

}
