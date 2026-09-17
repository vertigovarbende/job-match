package com.deveyk.jobmatch.job.application.port.in.query;

import com.deveyk.jobmatch.shared.domain.EmploymentType;
import com.deveyk.jobmatch.shared.domain.Seniority;
import com.deveyk.jobmatch.shared.domain.WorkplaceType;

import java.math.BigDecimal;
import java.util.Set;

public record JobSearchCriteria(
        String title,
        Seniority seniority,
        EmploymentType employmentType,
        WorkplaceType workplaceType,
        String locationCountry,
        String locationCity,
        BigDecimal salaryMin,
        Set<Long> skillIds
) {

}
