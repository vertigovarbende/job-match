package com.deveyk.jobmatch.candidate.presentation.rest.response;

public record SalaryRangeResponse(
        MoneyResponse min,
        MoneyResponse max
) {

}
