package com.deveyk.jobmatch.shared.presentation.rest.response;

import java.math.BigDecimal;

public record MoneyResponse(
        BigDecimal amount,
        String currency
) {

}
