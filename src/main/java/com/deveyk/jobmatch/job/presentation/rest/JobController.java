package com.deveyk.jobmatch.job.presentation.rest;

import com.deveyk.jobmatch.identity.application.CurrentUserFacade;
import com.deveyk.jobmatch.identity.domain.model.JmUser;
import com.deveyk.jobmatch.job.application.port.in.JobUseCase;
import com.deveyk.jobmatch.job.application.port.in.command.ArchiveJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.CloseJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.CreateJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.PublishJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.UpdateJobCommand;
import com.deveyk.jobmatch.job.application.port.in.query.JobListCriteria;
import com.deveyk.jobmatch.job.application.port.in.query.JobSearchCriteria;
import com.deveyk.jobmatch.job.application.port.in.query.JobSearchResult;
import com.deveyk.jobmatch.job.domain.model.Job;
import com.deveyk.jobmatch.job.presentation.rest.mapper.JobRequestMapper;
import com.deveyk.jobmatch.job.presentation.rest.mapper.JobResponseMapper;
import com.deveyk.jobmatch.job.presentation.rest.request.CreateJobRequest;
import com.deveyk.jobmatch.job.presentation.rest.request.JobListRequest;
import com.deveyk.jobmatch.job.presentation.rest.request.JobSearchRequest;
import com.deveyk.jobmatch.job.presentation.rest.request.UpdateJobRequest;
import com.deveyk.jobmatch.job.presentation.rest.response.JobResponse;
import com.deveyk.jobmatch.shared.domain.exception.FieldInvalidException;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import com.deveyk.jobmatch.shared.presentation.response.BaseResponse;
import com.deveyk.jobmatch.shared.presentation.response.JmPageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class JobController {

    private final JobUseCase jobUseCase;
    private final JobRequestMapper jobRequestMapper;
    private final JobResponseMapper jobResponseMapper;
    private final CurrentUserFacade currentUserFacade;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(JobApiPaths.BASE)
    public BaseResponse<JobResponse> createJob(@Valid @RequestBody final CreateJobRequest request) {

        final CreateJobCommand command = this.jobRequestMapper.toCommand(request);
        final Job job = this.jobUseCase.createJob(command);

        return BaseResponse.success(this.jobResponseMapper.toResponse(job));
    }

    @GetMapping(JobApiPaths.BY_ID)
    public BaseResponse<JobResponse> getJobById(@PathVariable final Long jobId) {

        final Job job = this.jobUseCase.getJobById(jobId);

        return BaseResponse.success(this.jobResponseMapper.toResponse(job));
    }

    @PutMapping(JobApiPaths.BY_ID)
    public BaseResponse<JobResponse> updateJob(@PathVariable final Long jobId, @Valid @RequestBody final UpdateJobRequest request) {

        final UpdateJobCommand command = this.jobRequestMapper.toCommand(request, jobId);
        final Job job = this.jobUseCase.updateJob(command);

        return BaseResponse.success(this.jobResponseMapper.toResponse(job));
    }

    @PostMapping(JobApiPaths.PUBLISH)
    public BaseResponse<JobResponse> publishJob(@PathVariable final Long jobId) {

        final JmUser currentUser = this.currentUserFacade.resolveCurrentUser();
        final PublishJobCommand command = this.jobRequestMapper.toPublishCommand(jobId, String.valueOf(currentUser.getId()));
        final Job job = this.jobUseCase.publishJob(command);

        return BaseResponse.success(this.jobResponseMapper.toResponse(job));
    }

    @PostMapping(JobApiPaths.CLOSE)
    public BaseResponse<JobResponse> closeJob(@PathVariable final Long jobId) {

        final JmUser currentUser = this.currentUserFacade.resolveCurrentUser();
        final CloseJobCommand command = this.jobRequestMapper.toCloseCommand(jobId, String.valueOf(currentUser.getId()));
        final Job job = this.jobUseCase.closeJob(command);

        return BaseResponse.success(this.jobResponseMapper.toResponse(job));
    }

    @PostMapping(JobApiPaths.ARCHIVE)
    public BaseResponse<JobResponse> archiveJob(@PathVariable final Long jobId) {

        final JmUser currentUser = this.currentUserFacade.resolveCurrentUser();
        final ArchiveJobCommand command = this.jobRequestMapper.toArchiveCommand(jobId, String.valueOf(currentUser.getId()));
        final Job job = this.jobUseCase.archiveJob(command);

        return BaseResponse.success(this.jobResponseMapper.toResponse(job));
    }

    @GetMapping(JobApiPaths.SEARCH)
    public BaseResponse<JmPageResponse<JobResponse>> searchPublishedJobs(@Valid final JobSearchRequest request) {

        if (!request.isOrderPropertyAccepted()) {
            throw new FieldInvalidException("sort", "must be one of the accepted properties");
        }

        final JobSearchCriteria criteria = this.jobRequestMapper.toCriteria(request);
        final JmPage<JobSearchResult> page = this.jobUseCase.searchPublishedJobs(criteria, request.getPageable().toPageable());
        final List<JobResponse> content = this.jobResponseMapper.toResponseListFromSearchResults(page.getContent());

        final JmPageResponse<JobResponse> response = JmPageResponse.<JobResponse>builder()
                .of(page, content)
                .build();

        return BaseResponse.success(response);
    }

    @GetMapping(JobApiPaths.BASE)
    public BaseResponse<JmPageResponse<JobResponse>> listMyJobs(@Valid final JobListRequest request) {

        if (!request.isOrderPropertyAccepted()) {
            throw new FieldInvalidException("sort", "must be one of the accepted properties");
        }

        final JobListCriteria criteria = this.jobRequestMapper.toCriteria(request);
        final JmPage<Job> page = this.jobUseCase.listMyJobs(criteria, request.getPageable().toPageable());
        final List<JobResponse> content = this.jobResponseMapper.toResponseList(page.getContent());

        final JmPageResponse<JobResponse> response = JmPageResponse.<JobResponse>builder()
                .of(page, content)
                .build();

        return BaseResponse.success(response);
    }

}
