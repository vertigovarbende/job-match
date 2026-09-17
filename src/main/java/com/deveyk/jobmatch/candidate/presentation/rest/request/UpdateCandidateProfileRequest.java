package com.deveyk.jobmatch.candidate.presentation.rest.request;

import com.deveyk.jobmatch.shared.presentation.rest.request.LocationRequest;
import com.deveyk.jobmatch.shared.presentation.rest.request.SalaryRangeRequest;
import jakarta.validation.Valid;

public record UpdateCandidateProfileRequest(
        String headline,

        String summary,

        @Valid
        LocationRequest location,

        @Valid
        WorkplacePreferencesRequest workplacePreferences,

        @Valid
        SalaryRangeRequest desiredSalary
) {

}
