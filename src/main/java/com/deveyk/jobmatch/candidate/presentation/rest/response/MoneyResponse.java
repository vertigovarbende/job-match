package com.deveyk.jobmatch.candidate.presentation.rest.response;

import java.math.BigDecimal;

public record MoneyResponse(
        BigDecimal amount,
        String currency
) {

}
