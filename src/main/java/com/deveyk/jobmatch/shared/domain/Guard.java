package com.deveyk.jobmatch.shared.domain;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Guard {

    public static String requireNonBlank(final String value, final String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }

}
