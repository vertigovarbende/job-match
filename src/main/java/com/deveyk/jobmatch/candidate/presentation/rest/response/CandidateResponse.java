package com.deveyk.jobmatch.candidate.presentation.rest.response;

import com.deveyk.jobmatch.shared.presentation.rest.response.LocationResponse;

public record CandidateResponse(
        Long id,
        Long userId,
        String headline,
        String summary,
        LocationResponse location,
        SalaryRangeResponse desiredSalary,
        WorkplacePreferencesResponse workplacePreferences
) {

}
