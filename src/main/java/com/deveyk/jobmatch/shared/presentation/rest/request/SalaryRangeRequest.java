package com.deveyk.jobmatch.shared.presentation.rest.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record SalaryRangeRequest(
        @Valid
        @NotNull
        MoneyRequest min,

        @Valid
        @NotNull
        MoneyRequest max
) {

}
