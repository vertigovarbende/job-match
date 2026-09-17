package com.deveyk.jobmatch.company.presentation.rest.response;

import com.deveyk.jobmatch.company.domain.CompanySize;
import com.deveyk.jobmatch.shared.presentation.rest.response.LocationResponse;

public record CompanyResponse(

        Long id,

        String name,

        String description,

        String industry,

        String website,

        CompanySize size,

        LocationResponse headquarters,

        boolean verified

) {

}
