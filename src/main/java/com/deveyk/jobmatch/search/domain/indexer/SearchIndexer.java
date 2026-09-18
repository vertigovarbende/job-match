package com.deveyk.jobmatch.search.domain.indexer;

public interface SearchIndexer<D> {

    D toDocument(String targetId);

    void indexDocument(D document);

    void remove(String targetId);

    String targetType();

    default void index(String targetId) {
        indexDocument(toDocument(targetId));
    }

}
