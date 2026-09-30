package com.deveyk.jobmatch.candidate.presentation.rest.mapper;

import com.deveyk.jobmatch.candidate.application.port.in.command.AddExperienceCommand;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateExperienceCommand;
import com.deveyk.jobmatch.candidate.presentation.rest.request.AddExperienceRequest;
import com.deveyk.jobmatch.candidate.presentation.rest.request.UpdateExperienceRequest;
import com.deveyk.jobmatch.shared.presentation.rest.mapper.CommonRequestMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExperienceRequestMapper extends CommonRequestMapper {

    AddExperienceCommand toCommand(AddExperienceRequest request, Long candidateId);

    UpdateExperienceCommand toCommand(UpdateExperienceRequest request, Long candidateId, Long experienceId);

}
