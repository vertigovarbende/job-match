package com.deveyk.jobmatch.candidate.presentation.rest.request;

import com.deveyk.jobmatch.shared.domain.WorkplaceType;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record WorkplacePreferencesRequest(
        @NotEmpty
        Set<WorkplaceType> acceptedTypes
) {

}
