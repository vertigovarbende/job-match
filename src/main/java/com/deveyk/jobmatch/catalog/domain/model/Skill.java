package com.deveyk.jobmatch.catalog.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public final class Skill {

    private final Long id;
    private final String name;

}