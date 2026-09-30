package com.deveyk.jobmatch.job.application.port.in.query;

import com.deveyk.jobmatch.shared.domain.EmploymentType;
import com.deveyk.jobmatch.shared.domain.Seniority;
import com.deveyk.jobmatch.shared.domain.WorkplaceType;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Set;

@Builder
public record JobSearchCriteria(
        String q,
        Seniority seniority,
        EmploymentType employmentType,
        WorkplaceType workplaceType,
        String locationCountry,
        String locationCity,
        BigDecimal salaryMin,
        Set<Long> skillIds
) {

}
