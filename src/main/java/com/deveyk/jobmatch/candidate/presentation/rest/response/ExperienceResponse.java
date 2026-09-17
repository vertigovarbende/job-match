package com.deveyk.jobmatch.candidate.presentation.rest.response;

import com.deveyk.jobmatch.shared.domain.EmploymentType;
import com.deveyk.jobmatch.shared.presentation.rest.response.LocationResponse;

import java.time.LocalDate;

public record ExperienceResponse(
        Long id,
        Long candidateId,
        String title,
        String company,
        LocationResponse location,
        EmploymentType employmentType,
        LocalDate startDate,
        LocalDate endDate,
        String description
) {

}
