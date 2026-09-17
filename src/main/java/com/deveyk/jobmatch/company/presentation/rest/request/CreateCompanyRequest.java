package com.deveyk.jobmatch.company.presentation.rest.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCompanyRequest(

        @NotBlank
        String name

) {

}
