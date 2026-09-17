package com.deveyk.jobmatch.candidate.presentation.rest.request;

import com.deveyk.jobmatch.candidate.domain.ProficiencyLevel;
import jakarta.validation.constraints.NotNull;

public record UpdateCandidateLanguageRequest(
        @NotNull
        ProficiencyLevel proficiencyLevel
) {

}
