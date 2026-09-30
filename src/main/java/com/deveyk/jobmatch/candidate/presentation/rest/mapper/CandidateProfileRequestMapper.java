package com.deveyk.jobmatch.candidate.presentation.rest.mapper;

import com.deveyk.jobmatch.candidate.application.port.in.command.CreateCandidateProfileCommand;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateCandidateProfileCommand;
import com.deveyk.jobmatch.candidate.domain.model.WorkplacePreferences;
import com.deveyk.jobmatch.candidate.presentation.rest.request.UpdateCandidateProfileRequest;
import com.deveyk.jobmatch.candidate.presentation.rest.request.WorkplacePreferencesRequest;
import com.deveyk.jobmatch.identity.domain.Role;
import com.deveyk.jobmatch.shared.presentation.rest.mapper.CommonRequestMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateProfileRequestMapper extends CommonRequestMapper {

    CreateCandidateProfileCommand toCommand(Long userId, Role role);

    UpdateCandidateProfileCommand toCommand(UpdateCandidateProfileRequest request, Long userId);

    default WorkplacePreferences toWorkplacePreferences(final WorkplacePreferencesRequest request) {
        if (request == null) {
            return null;
        }

        return new WorkplacePreferences(request.acceptedTypes());
    }

}
