package com.deveyk.jobmatch.candidate.presentation.rest.response;

import com.deveyk.jobmatch.shared.domain.WorkplaceType;

import java.util.Set;

public record WorkplacePreferencesResponse(
        Set<WorkplaceType> acceptedTypes
) {

}
