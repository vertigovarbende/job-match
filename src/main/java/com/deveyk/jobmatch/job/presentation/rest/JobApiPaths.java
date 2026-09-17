package com.deveyk.jobmatch.job.presentation.rest;

import com.deveyk.jobmatch.shared.presentation.rest.ApiPaths;

public final class JobApiPaths {

    private JobApiPaths() {
    }

    public static final String BASE = ApiPaths.BASE_PATH + "/jobs";
    public static final String SEARCH = BASE + "/search";
    public static final String BY_ID = BASE + "/{jobId}";
    public static final String PUBLISH = BY_ID + "/publish";
    public static final String CLOSE = BY_ID + "/close";
    public static final String ARCHIVE = BY_ID + "/archive";

}
