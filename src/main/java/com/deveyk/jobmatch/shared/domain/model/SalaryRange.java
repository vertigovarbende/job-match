package com.deveyk.jobmatch.shared.domain.model;

import com.deveyk.jobmatch.shared.domain.exception.FieldInvalidException;
import com.deveyk.jobmatch.shared.domain.exception.InvalidSalaryRangeException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public final class SalaryRange {

    private final Money min;
    private final Money max;

    public SalaryRange(final Money min, final Money max) {

        if (min == null) {
            throw new FieldInvalidException("min", "must not be null");
        }

        if (max == null) {
            throw new FieldInvalidException("max", "must not be null");
        }

        if (!min.sameCurrency(max)) {
            throw new InvalidSalaryRangeException("Salary range min and max must use the same currency");
        }

        if (min.getAmount().compareTo(max.getAmount()) > 0) {
            throw new InvalidSalaryRangeException("Salary range minimum must not exceed maximum");
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