package com.deveyk.jobmatch.company.presentation.rest.request;

import com.deveyk.jobmatch.company.domain.CompanySize;
import com.deveyk.jobmatch.shared.presentation.rest.request.LocationRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;


public record UpdateCompanyProfileRequest(

        @NotBlank
        String name,

        String description,

        String industry,

        String website,

        CompanySize size,

        @Valid
        LocationRequest headquarters

) {

}
