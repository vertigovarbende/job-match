package com.deveyk.jobmatch.candidate.presentation.rest.request;

import com.deveyk.jobmatch.candidate.domain.ProficiencyLevel;
import jakarta.validation.constraints.NotNull;

public record AttachCandidateLanguageRequest(
        @NotNull
        Long languageId,

        @NotNull
        ProficiencyLevel proficiencyLevel
) {

}
