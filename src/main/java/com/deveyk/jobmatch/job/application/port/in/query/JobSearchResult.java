package com.deveyk.jobmatch.job.application.port.in.query;

import com.deveyk.jobmatch.job.domain.model.Job;

import java.util.List;
import java.util.Map;

public record JobSearchResult(
        Job job,
        Map<String, List<String>> highlights
) {

}
