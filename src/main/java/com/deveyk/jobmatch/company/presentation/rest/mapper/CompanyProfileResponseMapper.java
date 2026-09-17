package com.deveyk.jobmatch.company.presentation.rest.mapper;

import com.deveyk.jobmatch.company.domain.model.Company;
import com.deveyk.jobmatch.company.presentation.rest.response.CompanyResponse;
import com.deveyk.jobmatch.shared.domain.model.Location;
import com.deveyk.jobmatch.shared.presentation.rest.response.LocationResponse;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CompanyProfileResponseMapper {

    CompanyResponse toResponse(Company company);

    default LocationResponse toLocationResponse(final Location location) {
        if (location == null) {
            return null;
        }

        return new LocationResponse(location.getCountry(), location.getCity());
    }

}
