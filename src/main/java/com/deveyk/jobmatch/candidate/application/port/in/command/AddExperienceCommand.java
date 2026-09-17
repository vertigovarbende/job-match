
package com.deveyk.jobmatch.candidate.application.port.in.command;

import com.deveyk.jobmatch.shared.domain.EmploymentType;
import com.deveyk.jobmatch.shared.domain.model.Location;

import java.time.LocalDate;

public record AddExperienceCommand(
        Long candidateId,
        String title,
        String company,
        Location location,
        EmploymentType employmentType,
        LocalDate startDate,
        LocalDate endDate,
        String description
) {


}