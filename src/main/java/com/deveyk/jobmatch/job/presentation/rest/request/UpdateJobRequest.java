package com.deveyk.jobmatch.job.presentation.rest.request;

import com.deveyk.jobmatch.shared.domain.EmploymentType;
import com.deveyk.jobmatch.shared.domain.Seniority;
import com.deveyk.jobmatch.shared.domain.WorkplaceType;
import com.deveyk.jobmatch.shared.presentation.rest.request.LocationRequest;
import com.deveyk.jobmatch.shared.presentation.rest.request.SalaryRangeRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.Set;

public record UpdateJobRequest(
        @NotBlank
        String title,

        String description,

        Seniority seniority,

        EmploymentType employmentType,

        WorkplaceType workplaceType,

        @Valid
        LocationRequest location,

        @Valid
        SalaryRangeRequest salaryRange,

        Integer minimumExperience,

        LocalDateTime expiresAt,

        Set<Long> requiredSkillIds,

        Set<Long> preferredSkillIds
) {

}
