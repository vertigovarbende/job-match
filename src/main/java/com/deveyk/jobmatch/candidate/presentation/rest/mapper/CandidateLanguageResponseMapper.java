package com.deveyk.jobmatch.candidate.presentation.rest.mapper;

import com.deveyk.jobmatch.candidate.domain.model.CandidateLanguage;
import com.deveyk.jobmatch.candidate.presentation.rest.response.CandidateLanguageResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CandidateLanguageResponseMapper {

    CandidateLanguageResponse toResponse(CandidateLanguage candidateLanguage);

    List<CandidateLanguageResponse> toResponseList(List<CandidateLanguage> candidateLanguages);

}
