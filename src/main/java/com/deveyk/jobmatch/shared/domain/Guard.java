package com.deveyk.jobmatch.shared.domain;

import com.deveyk.jobmatch.shared.domain.exception.FieldInvalidException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Guard {

    public static String requireNonBlank(final String value, final String fieldName) {
        if (value == null || value.isBlank()) {
            throw new FieldInvalidException(fieldName);
        }
        return value;
    }

}
