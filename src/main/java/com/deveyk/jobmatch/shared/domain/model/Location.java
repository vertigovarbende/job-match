package com.deveyk.jobmatch.shared.domain.model;

import com.deveyk.jobmatch.shared.domain.exception.FieldInvalidException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public final class Location {

    private final String country;
    private final String city;

    public Location(final String country, final String city) {

        if (country == null || country.isBlank()) {
            throw new FieldInvalidException("country", "must not be blank");
        }

        if (city == null || city.isBlank()) {
            throw new FieldInvalidException("city", "must not be blank");
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
