package com.deveyk.jobmatch.company.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompanySize {

    MICRO(1, 10),
    SMALL(11, 50),
    SMALL_MEDIUM(51, 200),
    MEDIUM(201, 500),
    MEDIUM_LARGE(501, 1000),
    LARGE(1001, 5000),
    VERY_LARGE(5001, 10000),
    ENTERPRISE(10001, null);

    private final int min;
    private final Integer max;

}
