package com.deveyk.jobmatch.company.presentation.rest.request;

import jakarta.validation.constraints.NotNull;

public record AddCompanyMembershipRequest(

        @NotNull
        Long userId

) {

}
