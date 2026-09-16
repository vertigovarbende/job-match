package com.deveyk.jobmatch.candidate.domain.model;

import com.deveyk.jobmatch.shared.domain.model.JmBaseDomain;
import com.deveyk.jobmatch.shared.domain.model.Location;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public final class Candidate extends JmBaseDomain {

    private final Long id;
    private final Long userId;
    private String headline;
    private String summary;
    private Location location;
    private SalaryRange desiredSalary;
    private WorkplacePreferences workplacePreferences;

    public static Candidate create(final Long userId) {
        return Candidate.builder()
                .id(null)
                .userId(Objects.requireNonNull(userId, "userId must not be null"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void updateProfile(final String headline, final String summary, final Location location, final WorkplacePreferences workplacePreferences, final SalaryRange desiredSalary) {

        this.headline = headline;
        this.summary = summary;
        this.location = location;
        this.workplacePreferences = workplacePreferences;
        this.desiredSalary = desiredSalary;

    }

    public boolean isOwnedBy(final Long userId) {
        return this.userId.equals(userId);
    }

}