package com.deveyk.jobmatch.candidate.presentation.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateCertificationRequest(
        @NotBlank
        String name,

        @NotBlank
        String issuingOrganization,

        @NotNull
        LocalDate issueDate,

        LocalDate expiryDate,

        String credentialId,

        String credentialUrl,

        String description
) {

}
