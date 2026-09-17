package com.deveyk.jobmatch.shared.presentation.rest.request;

import jakarta.validation.constraints.NotBlank;


public record LocationRequest(
        @NotBlank
        String country,

        @NotBlank
        String city
) {

}
