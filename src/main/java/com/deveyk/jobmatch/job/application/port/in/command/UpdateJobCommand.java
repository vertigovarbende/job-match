package com.deveyk.jobmatch.job.application.port.in.command;

import com.deveyk.jobmatch.shared.domain.EmploymentType;
import com.deveyk.jobmatch.shared.domain.Seniority;
import com.deveyk.jobmatch.shared.domain.WorkplaceType;
import com.deveyk.jobmatch.shared.domain.model.Location;
import com.deveyk.jobmatch.shared.domain.model.SalaryRange;

import java.time.LocalDateTime;
import java.util.Set;

public record UpdateJobCommand(
        Long id,
        String title,
        String description,
        Seniority seniority,
        EmploymentType employmentType,
        WorkplaceType workplaceType,
        Location location,
        SalaryRange salaryRange,
        Integer minimumExperience,
        LocalDateTime expiresAt,
        Set<Long> requiredSkillIds,
        Set<Long> preferredSkillIds
) {

}
