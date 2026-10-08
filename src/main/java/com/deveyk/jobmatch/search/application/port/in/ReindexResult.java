package com.deveyk.jobmatch.search.application.port.in;

import lombok.Builder;

@Builder
public record ReindexResult(
        String targetType,
        int total,
        int indexed,
        int failed
) {





}
