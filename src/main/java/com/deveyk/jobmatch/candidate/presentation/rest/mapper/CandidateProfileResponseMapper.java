package com.deveyk.jobmatch.candidate.presentation.rest.mapper;

import com.deveyk.jobmatch.candidate.domain.model.Candidate;
import com.deveyk.jobmatch.candidate.domain.model.Money;
import com.deveyk.jobmatch.candidate.domain.model.SalaryRange;
import com.deveyk.jobmatch.candidate.domain.model.WorkplacePreferences;
import com.deveyk.jobmatch.candidate.presentation.rest.response.CandidateResponse;
import com.deveyk.jobmatch.candidate.presentation.rest.response.MoneyResponse;
import com.deveyk.jobmatch.candidate.presentation.rest.response.SalaryRangeResponse;
import com.deveyk.jobmatch.candidate.presentation.rest.response.WorkplacePreferencesResponse;
import com.deveyk.jobmatch.shared.domain.model.Location;
import com.deveyk.jobmatch.shared.presentation.rest.response.LocationResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateProfileResponseMapper {

    CandidateResponse toResponse(Candidate candidate);

    default LocationResponse toLocationResponse(final Location location) {
        if (location == null) {
            return null;
        }

        return new LocationResponse(location.getCountry(), location.getCity());
    }

    default MoneyResponse toMoneyResponse(final Money money) {
        if (money == null) {
            return null;
        }

        return new MoneyResponse(money.getAmount(), money.getCurrency());
    }

    default SalaryRangeResponse toSalaryRangeResponse(final SalaryRange salaryRange) {
        if (salaryRange == null) {
            return null;
        }

        return new SalaryRangeResponse(this.toMoneyResponse(salaryRange.getMin()), this.toMoneyResponse(salaryRange.getMax()));
    }

    default WorkplacePreferencesResponse toWorkplacePreferencesResponse(final WorkplacePreferences workplacePreferences) {
        if (workplacePreferences == null) {
            return null;
        }

        return new WorkplacePreferencesResponse(workplacePreferences.getAcceptedTypes());
    }

}
