package com.deveyk.jobmatch.job.presentation.rest.mapper;

import com.deveyk.jobmatch.job.application.port.in.command.ArchiveJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.CloseJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.CreateJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.PublishJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.UpdateJobCommand;
import com.deveyk.jobmatch.job.application.port.in.query.JobListCriteria;
import com.deveyk.jobmatch.job.application.port.in.query.JobSearchCriteria;
import com.deveyk.jobmatch.job.presentation.rest.request.CreateJobRequest;
import com.deveyk.jobmatch.job.presentation.rest.request.JobListRequest;
import com.deveyk.jobmatch.job.presentation.rest.request.JobSearchRequest;
import com.deveyk.jobmatch.job.presentation.rest.request.UpdateJobRequest;
import com.deveyk.jobmatch.shared.presentation.rest.mapper.CommonRequestMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobRequestMapper extends CommonRequestMapper {

    CreateJobCommand toCommand(CreateJobRequest request);

    UpdateJobCommand toCommand(UpdateJobRequest request, Long id);

    PublishJobCommand toPublishCommand(Long id, String actorId);

    CloseJobCommand toCloseCommand(Long id, String actorId);

    ArchiveJobCommand toArchiveCommand(Long id, String actorId);

    JobSearchCriteria toCriteria(JobSearchRequest request);

    JobListCriteria toCriteria(JobListRequest request);

}
