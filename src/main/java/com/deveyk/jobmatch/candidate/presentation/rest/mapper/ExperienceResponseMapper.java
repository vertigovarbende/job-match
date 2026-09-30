package com.deveyk.jobmatch.candidate.presentation.rest.mapper;

import com.deveyk.jobmatch.candidate.domain.model.Experience;
import com.deveyk.jobmatch.candidate.presentation.rest.response.ExperienceResponse;
import com.deveyk.jobmatch.shared.presentation.rest.mapper.CommonResponseMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExperienceResponseMapper extends CommonResponseMapper {

    ExperienceResponse toResponse(Experience experience);

    List<ExperienceResponse> toResponseList(List<Experience> experiences);

}
