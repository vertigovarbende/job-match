package com.deveyk.jobmatch.candidate.presentation.rest.request;

import com.deveyk.jobmatch.candidate.domain.EmploymentType;
import com.deveyk.jobmatch.shared.presentation.rest.request.LocationRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AddExperienceRequest(
        @NotBlank
        String title,

        @NotBlank
        String company,

        @Valid
        LocationRequest location,

        EmploymentType employmentType,

        @NotNull
        LocalDate startDate,

        LocalDate endDate,

        String description
) {

}
