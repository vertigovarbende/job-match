package com.deveyk.jobmatch.search.presentation.rest;

public final class SearchApiPaths {

    private SearchApiPaths() {
    }

    public static final String INTERNAL_BASE = "/internal/search";
    public static final String REINDEX = INTERNAL_BASE + "/reindex/{targetType}";

}
