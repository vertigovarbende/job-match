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
public final class Education extends JmBaseDomain implements Ongoing {

    private final Long id;
    private final Long candidateId;
    private String institution;
    private String degree;
    private String fieldOfStudy;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

    public static Education create(final Long candidateId, final String institution, final String degree,
                                   final String fieldOfStudy, final LocalDate startDate, final LocalDate endDate,
                                   final String description) {

        validate(institution, startDate, endDate);

        return Education.builder()
                .id(null)
                .candidateId(candidateId)
                .institution(institution)
                .degree(degree)
                .fieldOfStudy(fieldOfStudy)
                .startDate(startDate)
                .endDate(endDate)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void update(final String institution, final String degree, final String fieldOfStudy,
                       final LocalDate startDate, final LocalDate endDate, final String description) {

        validate(institution, startDate, endDate);

        this.institution = institution;
        this.degree = degree;
        this.fieldOfStudy = fieldOfStudy;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    public boolean isOwnedBy(final Long candidateId) {
        return this.candidateId.equals(candidateId);
    }

    private static void validate(final String institution, final LocalDate startDate, final LocalDate endDate) {

        if (institution == null || institution.isBlank()) {
            throw new CandidateFieldInvalidException("institution");
        }

        if (startDate == null) {
            throw new CandidateFieldInvalidException("startDate");
        }

        if (endDate != null && endDate.isBefore(startDate)) {
            throw new CandidateFieldInvalidException("endDate", "must be on or after startDate");
        }

    }

}