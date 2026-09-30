
package com.deveyk.jobmatch.candidate.application.port.in.command;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpdateCertificationCommand(
        Long candidateId,
        Long certificationId,
        String name,
        String issuingOrganization,
        LocalDate issueDate,
        LocalDate expiryDate,
        String credentialId,
        String credentialUrl,
        String description
) {

}
