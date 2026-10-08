package com.deveyk.jobmatch.search.presentation.rest.response;

public record ReindexResponse(
        String targetType,
        int total,
        int indexed,
        int failed
) {
}
