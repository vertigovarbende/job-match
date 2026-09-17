package com.deveyk.jobmatch.catalog.presentation.rest.mapper;

import com.deveyk.jobmatch.catalog.domain.model.Skill;
import com.deveyk.jobmatch.catalog.presentation.rest.response.SkillResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillResponseMapper {

    SkillResponse toResponse(Skill skill);

    List<SkillResponse> toResponseList(List<Skill> skills);

}
