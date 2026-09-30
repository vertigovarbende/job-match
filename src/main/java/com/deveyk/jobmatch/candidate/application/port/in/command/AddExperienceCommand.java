
package com.deveyk.jobmatch.candidate.application.port.in.command;

import com.deveyk.jobmatch.shared.domain.EmploymentType;
import com.deveyk.jobmatch.shared.domain.model.Location;
import lombok.Builder;

import java.time.LocalDate;

@Builder
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
