package com.deveyk.jobmatch.shared.infrastructure.persistence;

import lombok.experimental.UtilityClass;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

@UtilityClass
public class JmConstraintViolations {

    public static String extractConstraintName(final DataIntegrityViolationException violation) {
        Throwable cause = violation.getCause();

        while (cause != null) {
            if (cause instanceof ConstraintViolationException constraintViolation) {
                return constraintViolation.getConstraintName();
            }
            cause = cause.getCause();
        }

        return null;
    }

}
