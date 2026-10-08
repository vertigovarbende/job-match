package com.deveyk.jobmatch.job.presentation.rest.mapper;

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

    default JobStatusType toStatusType(final JobStatus status) {
        return status == null ? null : status.type();
    }

}
