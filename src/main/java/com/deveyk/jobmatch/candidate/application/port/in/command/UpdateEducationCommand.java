
package com.deveyk.jobmatch.candidate.application.port.in.command;

import java.time.LocalDate;

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