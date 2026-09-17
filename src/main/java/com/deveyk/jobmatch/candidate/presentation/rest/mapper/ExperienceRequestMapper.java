package com.deveyk.jobmatch.candidate.presentation.rest.mapper;

import com.deveyk.jobmatch.candidate.application.port.in.command.AddExperienceCommand;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateExperienceCommand;
import com.deveyk.jobmatch.candidate.presentation.rest.request.AddExperienceRequest;
import com.deveyk.jobmatch.candidate.presentation.rest.request.UpdateExperienceRequest;
import com.deveyk.jobmatch.shared.domain.model.Location;
import com.deveyk.jobmatch.shared.presentation.rest.request.LocationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExperienceRequestMapper {

    AddExperienceCommand toCommand(AddExperienceRequest request, Long candidateId);

    UpdateExperienceCommand toCommand(UpdateExperienceRequest request, Long candidateId, Long experienceId);

    default Location toLocation(final LocationRequest request) {
        if (request == null) {
            return null;
        }

        return new Location(request.country(), request.city());
    }

}
