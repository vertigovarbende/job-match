package com.deveyk.jobmatch.candidate.domain.model;

import com.deveyk.jobmatch.candidate.domain.exception.CandidateFieldInvalidException;
import com.deveyk.jobmatch.candidate.domain.exception.InvalidSalaryRangeException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public final class SalaryRange {

    private final Money min;
    private final Money max;

    public SalaryRange(final Money min, final Money max) {

        if (min == null) {
            throw new CandidateFieldInvalidException("min", "must not be null");
        }

        if (max == null) {
            throw new CandidateFieldInvalidException("max", "must not be null");
        }

        if (!min.sameCurrency(max)) {
            throw new InvalidSalaryRangeException("Desired salary range min and max must use the same currency");
        }

        if (min.getAmount().compareTo(max.getAmount()) > 0) {
            throw new InvalidSalaryRangeException("Desired salary range minimum must not exceed maximum");
        }

        this.min = min;
        this.max = max;
    }

    public boolean overlaps(final SalaryRange other) {

        if (other == null || !this.min.sameCurrency(other.min)) {
            return false;
        }

        return this.min.getAmount().compareTo(other.max.getAmount()) <= 0 && other.min.getAmount().compareTo(this.max.getAmount()) <= 0;
    }

}