package com.deveyk.jobmatch.job.presentation.rest.response;

import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import com.deveyk.jobmatch.shared.domain.EmploymentType;
import com.deveyk.jobmatch.shared.domain.Seniority;
import com.deveyk.jobmatch.shared.domain.WorkplaceType;
import com.deveyk.jobmatch.shared.presentation.rest.response.LocationResponse;
import com.deveyk.jobmatch.shared.presentation.rest.response.SalaryRangeResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record JobResponse(
        Long id,
        Long companyId,
        String title,
        String description,
        Seniority seniority,
        EmploymentType employmentType,
        WorkplaceType workplaceType,
        LocationResponse location,
        SalaryRangeResponse salaryRange,
        Integer minimumExperience,
        LocalDateTime expiresAt,
        LocalDateTime publishedAt,
        JobStatusType status,
        Set<Long> requiredSkillIds,
        Set<Long> preferredSkillIds,
        Map<String, List<String>> highlights
) {

}
