package com.deveyk.jobmatch.candidate.domain.model;

import java.time.LocalDate;

public interface Ongoing {

    LocalDate getEndDate();

    default boolean isCurrent() {
        return getEndDate() == null;
    }

}