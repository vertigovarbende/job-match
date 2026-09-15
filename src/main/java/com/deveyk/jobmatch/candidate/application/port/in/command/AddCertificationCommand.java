package com.deveyk.jobmatch.candidate.application.port.in.command;

import java.time.LocalDate;

public record AddCertificationCommand(
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