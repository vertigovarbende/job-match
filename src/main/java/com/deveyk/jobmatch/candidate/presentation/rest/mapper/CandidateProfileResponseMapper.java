package com.deveyk.jobmatch.candidate.presentation.rest.mapper;

import com.deveyk.jobmatch.candidate.domain.model.Candidate;
import com.deveyk.jobmatch.candidate.domain.model.WorkplacePreferences;
import com.deveyk.jobmatch.candidate.presentation.rest.response.CandidateResponse;
import com.deveyk.jobmatch.candidate.presentation.rest.response.WorkplacePreferencesResponse;
import com.deveyk.jobmatch.shared.presentation.rest.mapper.CommonResponseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateProfileResponseMapper extends CommonResponseMapper {

    CandidateResponse toResponse(Candidate candidate);

    default WorkplacePreferencesResponse toWorkplacePreferencesResponse(final WorkplacePreferences workplacePreferences) {
        if (workplacePreferences == null) {
            return null;
        }

        return new WorkplacePreferencesResponse(workplacePreferences.getAcceptedTypes());
    }

}
