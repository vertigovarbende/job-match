
package com.deveyk.jobmatch.candidate.application.port.in.command;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AddEducationCommand(
        Long candidateId,
        String institution,
        String degree,
        String fieldOfStudy,
        LocalDate startDate,
        LocalDate endDate,
        String description
) {

}
