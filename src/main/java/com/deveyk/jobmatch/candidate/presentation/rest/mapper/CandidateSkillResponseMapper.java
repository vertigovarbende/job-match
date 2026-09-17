package com.deveyk.jobmatch.candidate.presentation.rest.mapper;

import com.deveyk.jobmatch.candidate.domain.model.CandidateSkill;
import com.deveyk.jobmatch.candidate.presentation.rest.response.CandidateSkillResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CandidateSkillResponseMapper {

    CandidateSkillResponse toResponse(CandidateSkill candidateSkill);

    List<CandidateSkillResponse> toResponseList(List<CandidateSkill> candidateSkills);

}
