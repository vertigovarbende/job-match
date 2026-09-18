package com.deveyk.jobmatch.job.infrastructure.elasticsearch.filter;

import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.JobDocument;
import com.deveyk.jobmatch.search.infrastructure.elasticsearch.filter.JmSearchFilter;
import com.deveyk.jobmatch.shared.domain.EmploymentType;
import com.deveyk.jobmatch.shared.domain.Seniority;
import com.deveyk.jobmatch.shared.domain.WorkplaceType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Builder
public class JobListDocumentFilter implements JmSearchFilter<JobDocument> {

    private final Long companyId;
    private final JobStatusType status;
    private final String title;
    private final Seniority seniority;
    private final EmploymentType employmentType;
    private final WorkplaceType workplaceType;
    private final String locationCountry;
    private final String locationCity;
    private final BigDecimal salaryMin;
    private final Set<Long> skillIds;

    @Override
    public Query toQuery() {

        Criteria criteria = new Criteria("companyId").is(this.companyId);

        if (this.status != null) {
            criteria = criteria.and(new Criteria("status").is(this.status.name()));
        }

        if (this.title != null && !this.title.isBlank()) {
            criteria = criteria.and(new Criteria("title").matches(this.title));
        }

        if (this.seniority != null) {
            criteria = criteria.and(new Criteria("seniority").is(this.seniority.name()));
        }

        if (this.employmentType != null) {
            criteria = criteria.and(new Criteria("employmentType").is(this.employmentType.name()));
        }

        if (this.workplaceType != null) {
            criteria = criteria.and(new Criteria("workplaceType").is(this.workplaceType.name()));
        }

        if (this.locationCountry != null && !this.locationCountry.isBlank()) {
            criteria = criteria.and(new Criteria("locationCountry").is(this.locationCountry));
        }

        if (this.locationCity != null && !this.locationCity.isBlank()) {
            criteria = criteria.and(new Criteria("locationCity").is(this.locationCity));
        }

        if (this.salaryMin != null) {
            criteria = criteria.and(new Criteria("salaryMaxAmount").greaterThanEqual(this.salaryMin));
        }

        if (this.skillIds != null && !this.skillIds.isEmpty()) {
            criteria = criteria.and(Criteria.or()
                    .or(new Criteria("requiredSkillIds").in(this.skillIds))
                    .or(new Criteria("preferredSkillIds").in(this.skillIds)));
        }

        return new CriteriaQuery(criteria);
    }

}
