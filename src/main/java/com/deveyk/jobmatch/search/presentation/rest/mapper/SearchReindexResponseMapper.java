package com.deveyk.jobmatch.search.presentation.rest.mapper;

import com.deveyk.jobmatch.search.application.port.in.ReindexResult;
import com.deveyk.jobmatch.search.presentation.rest.response.ReindexResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SearchReindexResponseMapper {

    ReindexResponse toResponse(ReindexResult result);

}
