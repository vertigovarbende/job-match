package com.deveyk.jobmatch.candidate.domain.model;

import com.deveyk.jobmatch.candidate.domain.exception.CandidateFieldInvalidException;
import com.deveyk.jobmatch.shared.domain.model.JmBaseDomain;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public final class Certification extends JmBaseDomain {

    private final Long id;
    private final Long candidateId;
    private String name;
    private String issuingOrganization;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String credentialId;
    private String credentialUrl;
    private String description;

    public static Certification create(final Long candidateId, final String name, final String issuingOrganization,
                                       final LocalDate issueDate, final LocalDate expiryDate,
                                       final String credentialId, final String credentialUrl, final String description) {

        validate(name, issuingOrganization, issueDate, expiryDate);

        return Certification.builder()
                .id(null)
                .candidateId(candidateId)
                .name(name)
                .issuingOrganization(issuingOrganization)
                .issueDate(issueDate)
                .expiryDate(expiryDate)
                .credentialId(credentialId)
                .credentialUrl(credentialUrl)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void update(final String name, final String issuingOrganization, final LocalDate issueDate,
                       final LocalDate expiryDate, final String credentialId, final String credentialUrl,
                       final String description) {

        validate(name, issuingOrganization, issueDate, expiryDate);

        this.name = name;
        this.issuingOrganization = issuingOrganization;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.credentialId = credentialId;
        this.credentialUrl = credentialUrl;
        this.description = description;
    }

    public boolean isOwnedBy(final Long candidateId) {
        return this.candidateId.equals(candidateId);
    }

    public boolean isExpired() {
        return this.expiryDate != null && this.expiryDate.isBefore(LocalDate.now());
    }

    private static void validate(final String name, final String issuingOrganization,
                                 final LocalDate issueDate, final LocalDate expiryDate) {

        if (name == null || name.isBlank()) {
            throw new CandidateFieldInvalidException("name");
        }

        if (issuingOrganization == null || issuingOrganization.isBlank()) {
            throw new CandidateFieldInvalidException("issuingOrganization");
        }

        if (issueDate == null) {
            throw new CandidateFieldInvalidException("issueDate");
        }

        if (expiryDate != null && expiryDate.isBefore(issueDate)) {
            throw new CandidateFieldInvalidException("expiryDate", "must be on or after issueDate");
        }

    }

}