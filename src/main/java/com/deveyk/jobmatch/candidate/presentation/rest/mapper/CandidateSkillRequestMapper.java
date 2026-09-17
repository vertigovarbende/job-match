package com.deveyk.jobmatch.candidate.presentation.rest.mapper;

import com.deveyk.jobmatch.candidate.application.port.in.command.AttachCandidateSkillCommand;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateCandidateSkillCommand;
import com.deveyk.jobmatch.candidate.presentation.rest.request.AttachCandidateSkillRequest;
import com.deveyk.jobmatch.candidate.presentation.rest.request.UpdateCandidateSkillRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateSkillRequestMapper {

    AttachCandidateSkillCommand toCommand(AttachCandidateSkillRequest request, Long candidateId);

    UpdateCandidateSkillCommand toCommand(UpdateCandidateSkillRequest request, Long candidateId, Long skillId);

}
