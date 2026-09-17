package com.deveyk.jobmatch.candidate.presentation.rest.response;

import java.time.LocalDate;

public record CertificationResponse(
        Long id,
        Long candidateId,
        String name,
        String issuingOrganization,
        LocalDate issueDate,
        LocalDate expiryDate,
        String credentialId,
        String credentialUrl,
        String description
) {

}
