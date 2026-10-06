package com.deveyk.jobmatch.search.domain.event;

import com.deveyk.jobmatch.search.domain.indexer.SearchIndexer;

import java.util.function.BiConsumer;

public enum IndexOperation {

    UPSERT(SearchIndexer::index),
    DELETE(SearchIndexer::remove);

    private final BiConsumer<SearchIndexer<?>, String> action;

    IndexOperation(final BiConsumer<SearchIndexer<?>, String> action) {
        this.action = action;
    }

    public void applyTo(final SearchIndexer<?> indexer, final String targetId) {
        this.action.accept(indexer, targetId);
    }

}
