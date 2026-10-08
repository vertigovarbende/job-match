package com.deveyk.jobmatch.search.presentation.rest;

import com.deveyk.jobmatch.search.application.port.in.ReindexResult;
import com.deveyk.jobmatch.search.application.port.in.SearchReindexUseCase;
import com.deveyk.jobmatch.search.presentation.rest.mapper.SearchReindexResponseMapper;
import com.deveyk.jobmatch.search.presentation.rest.response.ReindexResponse;
import com.deveyk.jobmatch.shared.presentation.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchReindexController {

    private final SearchReindexUseCase searchReindexUseCase;
    private final SearchReindexResponseMapper searchReindexResponseMapper;

    @PostMapping(SearchApiPaths.REINDEX)
    public BaseResponse<ReindexResponse> reindex(@PathVariable final String targetType) {

        final ReindexResult result = this.searchReindexUseCase.reindex(targetType);

        return BaseResponse.success(this.searchReindexResponseMapper.toResponse(result));
    }

}
