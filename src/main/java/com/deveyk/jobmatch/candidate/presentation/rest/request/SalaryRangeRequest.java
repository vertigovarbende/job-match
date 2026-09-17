package com.deveyk.jobmatch.candidate.presentation.rest.request;

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
