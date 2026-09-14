package com.deveyk.jobmatch.candidate.domain.model;

import com.deveyk.jobmatch.candidate.domain.WorkplaceType;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateFieldInvalidException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@Getter
@EqualsAndHashCode
public final class WorkplacePreferences {

    private final Set<WorkplaceType> acceptedTypes;

    public WorkplacePreferences(final Set<WorkplaceType> acceptedTypes) {

        if (acceptedTypes == null || acceptedTypes.isEmpty()) {
            throw new CandidateFieldInvalidException("acceptedTypes", "must not be null or empty");
        }

        this.acceptedTypes = Collections.unmodifiableSet(EnumSet.copyOf(acceptedTypes));
    }

    public boolean isOpenTo(final WorkplaceType type) {
        return type != null && this.acceptedTypes.contains(type);
    }

}