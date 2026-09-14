package com.deveyk.jobmatch.candidate.application.port.in;

import com.deveyk.jobmatch.candidate.domain.model.Location;
import com.deveyk.jobmatch.candidate.domain.model.SalaryRange;
import com.deveyk.jobmatch.candidate.domain.model.WorkplacePreferences;

public record UpdateCandidateProfileCommand(
        Long userId,
        String headline,
        String summary,
        Location location,
        WorkplacePreferences workplacePreferences,
        SalaryRange desiredSalary
) {

}