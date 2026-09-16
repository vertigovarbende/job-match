package com.deveyk.jobmatch.company.application.port.in.command;

import com.deveyk.jobmatch.company.domain.CompanySize;
import com.deveyk.jobmatch.shared.domain.model.Location;

public record UpdateCompanyProfileCommand(
        Long id,
        String name,
        String description,
        String industry,
        String website,
        CompanySize size,
        Location headquarters
) {

}
