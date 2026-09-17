package com.deveyk.jobmatch.candidate.presentation.rest.response;

import java.time.LocalDate;

public record EducationResponse(
        Long id,
        Long candidateId,
        String institution,
        String degree,
        String fieldOfStudy,
        LocalDate startDate,
        LocalDate endDate,
        String description
) {

}
