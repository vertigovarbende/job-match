package com.deveyk.jobmatch.job.presentation.rest.mapper;

import com.deveyk.jobmatch.job.application.port.in.query.JobSearchResult;
import com.deveyk.jobmatch.job.domain.model.Job;
import com.deveyk.jobmatch.job.domain.model.JobStatus;
import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import com.deveyk.jobmatch.job.presentation.rest.response.JobResponse;
import com.deveyk.jobmatch.shared.presentation.rest.mapper.CommonResponseMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JobResponseMapper extends CommonResponseMapper {

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

    default JobStatusType toStatusType(final JobStatus status) {
        return status == null ? null : status.type();
    }

}
