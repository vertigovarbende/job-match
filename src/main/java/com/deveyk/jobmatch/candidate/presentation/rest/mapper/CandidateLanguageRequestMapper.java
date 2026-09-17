package com.deveyk.jobmatch.candidate.presentation.rest.mapper;

import com.deveyk.jobmatch.candidate.application.port.in.command.AttachCandidateLanguageCommand;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateCandidateLanguageCommand;
import com.deveyk.jobmatch.candidate.presentation.rest.request.AttachCandidateLanguageRequest;
import com.deveyk.jobmatch.candidate.presentation.rest.request.UpdateCandidateLanguageRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateLanguageRequestMapper {

    AttachCandidateLanguageCommand toCommand(AttachCandidateLanguageRequest request, Long candidateId);

    UpdateCandidateLanguageCommand toCommand(UpdateCandidateLanguageRequest request, Long candidateId, Long languageId);

}
