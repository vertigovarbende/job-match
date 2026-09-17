package com.deveyk.jobmatch.shared.presentation.rest.response;

public record SalaryRangeResponse(
        MoneyResponse min,
        MoneyResponse max
) {

}
