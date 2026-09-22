package com.deveyk.jobmatch.job.presentation.rest.mapper;

import com.deveyk.jobmatch.job.application.port.in.query.JobSearchResult;
import com.deveyk.jobmatch.job.domain.model.Job;
import com.deveyk.jobmatch.job.domain.model.JobStatus;
import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import com.deveyk.jobmatch.job.presentation.rest.response.JobResponse;
import com.deveyk.jobmatch.shared.domain.model.Location;
import com.deveyk.jobmatch.shared.domain.model.Money;
import com.deveyk.jobmatch.shared.domain.model.SalaryRange;
import com.deveyk.jobmatch.shared.presentation.rest.response.LocationResponse;
import com.deveyk.jobmatch.shared.presentation.rest.response.MoneyResponse;
import com.deveyk.jobmatch.shared.presentation.rest.response.SalaryRangeResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JobResponseMapper {

    JobResponse toResponse(Job job);

    List<JobResponse> toResponseList(List<Job> jobs);

    default JobResponse toResponse(final JobSearchResult result) {

        final JobResponse base = this.toResponse(result.job());

        return new JobResponse(
                base.id(),
                base.companyId(),
                base.title(),
                base.description(),
                base.seniority(),
                base.employmentType(),
                base.workplaceType(),
                base.location(),
                base.salaryRange(),
                base.minimumExperience(),
                base.expiresAt(),
                base.publishedAt(),
                base.status(),
                base.requiredSkillIds(),
                base.preferredSkillIds(),
                result.highlights()
        );

    }

    default List<JobResponse> toResponseListFromSearchResults(final List<JobSearchResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    default LocationResponse toLocationResponse(final Location location) {
        if (location == null) {
            return null;
        }

        return new LocationResponse(location.getCountry(), location.getCity());
    }

    default MoneyResponse toMoneyResponse(final Money money) {
        if (money == null) {
            return null;
        }

        return new MoneyResponse(money.getAmount(), money.getCurrency());
    }

    default SalaryRangeResponse toSalaryRangeResponse(final SalaryRange salaryRange) {
        if (salaryRange == null) {
            return null;
        }

        return new SalaryRangeResponse(this.toMoneyResponse(salaryRange.getMin()), this.toMoneyResponse(salaryRange.getMax()));
    }

    default JobStatusType toStatusType(final JobStatus status) {
        return status == null ? null : status.type();
    }

}
