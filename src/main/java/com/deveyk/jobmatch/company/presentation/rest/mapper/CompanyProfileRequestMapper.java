package com.deveyk.jobmatch.company.presentation.rest.mapper;

import com.deveyk.jobmatch.company.application.port.in.command.CreateCompanyCommand;
import com.deveyk.jobmatch.company.application.port.in.command.UpdateCompanyProfileCommand;
import com.deveyk.jobmatch.company.application.port.in.command.VerifyCompanyCommand;
import com.deveyk.jobmatch.company.presentation.rest.request.CreateCompanyRequest;
import com.deveyk.jobmatch.company.presentation.rest.request.UpdateCompanyProfileRequest;
import com.deveyk.jobmatch.identity.domain.Role;
import com.deveyk.jobmatch.shared.domain.model.Location;
import com.deveyk.jobmatch.shared.presentation.rest.request.LocationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyProfileRequestMapper {

    CreateCompanyCommand toCommand(CreateCompanyRequest request, Role role);

    UpdateCompanyProfileCommand toCommand(UpdateCompanyProfileRequest request, Long id);

    VerifyCompanyCommand toCommand(Long id, String actorId);

    default Location toLocation(final LocationRequest request) {
        if (request == null) {
            return null;
        }

        return new Location(request.country(), request.city());
    }

}
