package com.deveyk.jobmatch.job.presentation.rest.mapper;

import com.deveyk.jobmatch.job.application.port.in.query.JobSearchResult;
import com.deveyk.jobmatch.job.presentation.rest.response.JobSearchHitResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = JobResponseMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface JobSearchResponseMapper {

    JobSearchHitResponse toResponse(JobSearchResult result);

    List<JobSearchHitResponse> toResponseList(List<JobSearchResult> results);

}
