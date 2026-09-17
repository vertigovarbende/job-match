package com.deveyk.jobmatch.candidate.presentation.rest.mapper;

import com.deveyk.jobmatch.candidate.domain.model.Education;
import com.deveyk.jobmatch.candidate.presentation.rest.response.EducationResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EducationResponseMapper {

    EducationResponse toResponse(Education education);

    List<EducationResponse> toResponseList(List<Education> educations);

}
