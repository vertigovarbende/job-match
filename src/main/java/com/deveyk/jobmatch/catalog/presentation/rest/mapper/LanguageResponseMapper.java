package com.deveyk.jobmatch.catalog.presentation.rest.mapper;

import com.deveyk.jobmatch.catalog.domain.model.Language;
import com.deveyk.jobmatch.catalog.presentation.rest.response.LanguageResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LanguageResponseMapper {

    LanguageResponse toResponse(Language language);

    List<LanguageResponse> toResponseList(List<Language> languages);

}
