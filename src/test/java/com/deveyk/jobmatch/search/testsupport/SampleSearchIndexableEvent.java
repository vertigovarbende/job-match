package com.deveyk.jobmatch.search.testsupport;

import com.deveyk.jobmatch.search.domain.event.IndexOperation;
import com.deveyk.jobmatch.search.domain.event.SearchIndexableEvent;

public record SampleSearchIndexableEvent(
        String targetType,
        String targetId,
        IndexOperation operation
) implements SearchIndexableEvent {

    public static SampleSearchIndexableEvent upsert(final String targetType, final String targetId) {
        return new SampleSearchIndexableEvent(targetType, targetId, IndexOperation.UPSERT);
    }

    public static SampleSearchIndexableEvent delete(final String targetType, final String targetId) {
        return new SampleSearchIndexableEvent(targetType, targetId, IndexOperation.DELETE);
    }

}
