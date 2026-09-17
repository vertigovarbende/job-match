package com.deveyk.jobmatch.candidate.presentation.rest.mapper;

import com.deveyk.jobmatch.candidate.application.port.in.command.CreateCandidateProfileCommand;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateCandidateProfileCommand;
import com.deveyk.jobmatch.shared.domain.model.Money;
import com.deveyk.jobmatch.shared.domain.model.SalaryRange;
import com.deveyk.jobmatch.candidate.domain.model.WorkplacePreferences;
import com.deveyk.jobmatch.shared.presentation.rest.request.MoneyRequest;
import com.deveyk.jobmatch.shared.presentation.rest.request.SalaryRangeRequest;
import com.deveyk.jobmatch.candidate.presentation.rest.request.UpdateCandidateProfileRequest;
import com.deveyk.jobmatch.candidate.presentation.rest.request.WorkplacePreferencesRequest;
import com.deveyk.jobmatch.identity.domain.Role;
import com.deveyk.jobmatch.shared.domain.model.Location;
import com.deveyk.jobmatch.shared.presentation.rest.request.LocationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateProfileRequestMapper {

    CreateCandidateProfileCommand toCommand(Long userId, Role role);

    UpdateCandidateProfileCommand toCommand(UpdateCandidateProfileRequest request, Long userId);

    default Location toLocation(final LocationRequest request) {
        if (request == null) {
            return null;
        }

        return new Location(request.country(), request.city());
    }

    default Money toMoney(final MoneyRequest request) {
        if (request == null) {
            return null;
        }

        return new Money(request.amount(), request.currency());
    }

    default SalaryRange toSalaryRange(final SalaryRangeRequest request) {
        if (request == null) {
            return null;
        }

        return new SalaryRange(this.toMoney(request.min()), this.toMoney(request.max()));
    }

    default WorkplacePreferences toWorkplacePreferences(final WorkplacePreferencesRequest request) {
        if (request == null) {
            return null;
        }

        return new WorkplacePreferences(request.acceptedTypes());
    }

}
