package com.deveyk.jobmatch.search.domain.indexer;

import java.util.List;

public interface SearchIndexer<D> {

    D toDocument(String targetId);

    void indexDocument(D document);

    void remove(String targetId);

    void removeAll();

    List<String> findIndexableTargetIds(int page, int size);

    String targetType();

    default void index(String targetId) {
        indexDocument(toDocument(targetId));
    }

}
