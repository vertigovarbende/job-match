package com.deveyk.jobmatch.candidate.presentation.rest.mapper;

import com.deveyk.jobmatch.candidate.domain.model.Experience;
import com.deveyk.jobmatch.candidate.presentation.rest.response.ExperienceResponse;
import com.deveyk.jobmatch.shared.domain.model.Location;
import com.deveyk.jobmatch.shared.presentation.rest.response.LocationResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExperienceResponseMapper {

    ExperienceResponse toResponse(Experience experience);

    List<ExperienceResponse> toResponseList(List<Experience> experiences);

    default LocationResponse toLocationResponse(final Location location) {
        if (location == null) {
            return null;
        }

        return new LocationResponse(location.getCountry(), location.getCity());
    }

}
