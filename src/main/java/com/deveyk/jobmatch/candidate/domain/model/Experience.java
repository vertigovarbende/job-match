package com.deveyk.jobmatch.candidate.domain.model;

import com.deveyk.jobmatch.candidate.domain.EmploymentType;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateFieldInvalidException;
import com.deveyk.jobmatch.shared.domain.model.JmBaseDomain;
import com.deveyk.jobmatch.shared.domain.model.Location;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public final class Experience extends JmBaseDomain implements Ongoing {

    private final Long id;
    private final Long candidateId;
    private String title;
    private String company;
    private Location location;
    private EmploymentType employmentType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

    public static Experience create(final Long candidateId, final String title, final String company,
                                    final Location location, final EmploymentType employmentType,
                                    final LocalDate startDate, final LocalDate endDate, final String description) {

        validate(title, company, startDate, endDate);

        return Experience.builder()
                .id(null)
                .candidateId(candidateId)
                .title(title)
                .company(company)
                .location(location)
                .employmentType(employmentType)
                .startDate(startDate)
                .endDate(endDate)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void update(final String title, final String company, final Location location,
                       final EmploymentType employmentType, final LocalDate startDate,
                       final LocalDate endDate, final String description) {

        validate(title, company, startDate, endDate);

        this.title = title;
        this.company = company;
        this.location = location;
        this.employmentType = employmentType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    public boolean isOwnedBy(final Long candidateId) {
        return this.candidateId.equals(candidateId);
    }

    private static void validate(final String title, final String company, final LocalDate startDate, final LocalDate endDate) {

        if (title == null || title.isBlank()) {
            throw new CandidateFieldInvalidException("title");
        }

        if (company == null || company.isBlank()) {
            throw new CandidateFieldInvalidException("company");
        }

        if (startDate == null) {
            throw new CandidateFieldInvalidException("startDate");
        }

        if (endDate != null && endDate.isBefore(startDate)) {
            throw new CandidateFieldInvalidException("endDate", "must be on or after startDate");
        }

    }

}