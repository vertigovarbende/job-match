package com.deveyk.jobmatch.candidate.domain.model;

import com.deveyk.jobmatch.candidate.domain.exception.CandidateFieldInvalidException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public final class Location {

    private final String country;
    private final String city;

    public Location(final String country, final String city) {

        if (country == null || country.isBlank()) {
            throw new CandidateFieldInvalidException("country", "must not be blank");
        }

        if (city == null || city.isBlank()) {
            throw new CandidateFieldInvalidException("city", "must not be blank");
        }

        this.country = country;
        this.city = city;
    }

    public boolean sameCity(final Location other) {
        if (other == null) {
            return false;
        }

        return this.country.equalsIgnoreCase(other.country) && this.city.equalsIgnoreCase(other.city);
    }

}