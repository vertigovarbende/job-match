
package com.deveyk.jobmatch.candidate.application.port.in.command;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpdateEducationCommand(
        Long candidateId,
        Long educationId,
        String institution,
        String degree,
        String fieldOfStudy,
        LocalDate startDate,
        LocalDate endDate,
        String description
) {

}
