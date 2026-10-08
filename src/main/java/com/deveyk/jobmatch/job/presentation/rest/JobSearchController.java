package com.deveyk.jobmatch.job.presentation.rest;

import com.deveyk.jobmatch.job.application.port.in.JobSearchUseCase;
import com.deveyk.jobmatch.job.application.port.in.query.JobSearchCriteria;
import com.deveyk.jobmatch.job.application.port.in.query.JobSearchResult;
import com.deveyk.jobmatch.job.presentation.rest.mapper.JobRequestMapper;
import com.deveyk.jobmatch.job.presentation.rest.mapper.JobSearchResponseMapper;
import com.deveyk.jobmatch.job.presentation.rest.request.JobSearchRequest;
import com.deveyk.jobmatch.job.presentation.rest.response.JobSearchHitResponse;
import com.deveyk.jobmatch.shared.domain.exception.FieldInvalidException;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import com.deveyk.jobmatch.shared.presentation.response.BaseResponse;
import com.deveyk.jobmatch.shared.presentation.response.JmPageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class JobSearchController {

    private final JobSearchUseCase jobSearchUseCase;
    private final JobRequestMapper jobRequestMapper;
    private final JobSearchResponseMapper jobSearchResponseMapper;

    @GetMapping(JobApiPaths.SEARCH)
    public BaseResponse<JmPageResponse<JobSearchHitResponse>> searchPublishedJobs(@Valid final JobSearchRequest request) {

        if (!request.isOrderPropertyAccepted()) {
            throw new FieldInvalidException("sort", "must be one of the accepted properties");
        }

        final JobSearchCriteria criteria = this.jobRequestMapper.toCriteria(request);
        final JmPage<JobSearchResult> page = this.jobSearchUseCase.searchPublishedJobs(criteria, request.getPageable().toPageable());
        final List<JobSearchHitResponse> content = this.jobSearchResponseMapper.toResponseList(page.getContent());

        final JmPageResponse<JobSearchHitResponse> response = JmPageResponse.<JobSearchHitResponse>builder()
                .of(page, content)
                .build();

        return BaseResponse.success(response);
    }

}
