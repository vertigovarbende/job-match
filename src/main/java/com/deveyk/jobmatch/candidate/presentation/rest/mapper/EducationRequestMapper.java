package com.deveyk.jobmatch.candidate.presentation.rest.mapper;

import com.deveyk.jobmatch.candidate.application.port.in.command.AddEducationCommand;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateEducationCommand;
import com.deveyk.jobmatch.candidate.presentation.rest.request.AddEducationRequest;
import com.deveyk.jobmatch.candidate.presentation.rest.request.UpdateEducationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EducationRequestMapper {

    AddEducationCommand toCommand(AddEducationRequest request, Long candidateId);

    UpdateEducationCommand toCommand(UpdateEducationRequest request, Long candidateId, Long educationId);

}
