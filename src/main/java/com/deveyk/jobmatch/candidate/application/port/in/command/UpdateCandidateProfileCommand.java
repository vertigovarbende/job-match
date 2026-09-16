package com.deveyk.jobmatch.candidate.application.port.in.command;

import com.deveyk.jobmatch.candidate.domain.model.SalaryRange;
import com.deveyk.jobmatch.candidate.domain.model.WorkplacePreferences;
import com.deveyk.jobmatch.shared.domain.model.Location;

public record UpdateCandidateProfileCommand(
        Long userId,
        String headline,
        String summary,
        Location location,
        WorkplacePreferences workplacePreferences,
        SalaryRange desiredSalary
) {

}