package com.deveyk.jobmatch.job.presentation.rest.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.List;
import java.util.Map;

public record JobSearchHitResponse(
        @JsonUnwrapped
        JobResponse job,
        Map<String, List<String>> highlights
) {

}
