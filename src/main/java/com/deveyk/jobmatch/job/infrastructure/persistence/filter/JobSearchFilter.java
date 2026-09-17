package com.deveyk.jobmatch.job.infrastructure.persistence.filter;

import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import com.deveyk.jobmatch.job.infrastructure.persistence.entity.JobEntity;
import com.deveyk.jobmatch.job.infrastructure.persistence.entity.JobSkillEntity;
import com.deveyk.jobmatch.shared.domain.EmploymentType;
import com.deveyk.jobmatch.shared.domain.Seniority;
import com.deveyk.jobmatch.shared.domain.WorkplaceType;
import com.deveyk.jobmatch.shared.infrastructure.persistence.JmFilter;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Builder
public class JobSearchFilter implements JmFilter<JobEntity> {

    private final String title;
    private final Seniority seniority;
    private final EmploymentType employmentType;
    private final WorkplaceType workplaceType;
    private final String locationCountry;
    private final String locationCity;
    private final BigDecimal salaryMin;
    private final Set<Long> skillIds;

    @Override
    public Specification<JobEntity> toSpecification() {

        Specification<JobEntity> specification = Specification.unrestricted();

        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), JobStatusType.PUBLISHED));

        if (this.title != null && !this.title.isBlank()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("title")), "%" + this.title.toUpperCase() + "%"));
        }

        if (this.seniority != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("seniority"), this.seniority));
        }

        if (this.employmentType != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("employmentType"), this.employmentType));
        }

        if (this.workplaceType != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("workplaceType"), this.workplaceType));
        }

        if (this.locationCountry != null && !this.locationCountry.isBlank()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("locationCountry"), this.locationCountry));
        }

        if (this.locationCity != null && !this.locationCity.isBlank()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("locationCity"), this.locationCity));
        }

        if (this.salaryMin != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("salaryMaxAmount"), this.salaryMin));
        }

        if (this.skillIds != null && !this.skillIds.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> {
                final Subquery<Long> subquery = query.subquery(Long.class);
                final Root<JobSkillEntity> jobSkill = subquery.from(JobSkillEntity.class);
                subquery.select(jobSkill.get("jobId"))
                        .where(jobSkill.get("skillId").in(this.skillIds));
                return root.get("id").in(subquery);
            });
        }

        return specification;
    }

}
