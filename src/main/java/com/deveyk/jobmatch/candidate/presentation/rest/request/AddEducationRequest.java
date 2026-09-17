package com.deveyk.jobmatch.candidate.presentation.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AddEducationRequest(
        @NotBlank
        String institution,

        String degree,

        String fieldOfStudy,

        @NotNull
        LocalDate startDate,

        LocalDate endDate,

        String description
) {

}
