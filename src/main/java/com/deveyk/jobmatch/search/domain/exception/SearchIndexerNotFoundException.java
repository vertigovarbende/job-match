package com.deveyk.jobmatch.search.domain.exception;

import com.deveyk.jobmatch.search.domain.SearchErrorCode;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchResourceNotFoundException;

import java.io.Serial;

public class SearchIndexerNotFoundException extends JobMatchResourceNotFoundException {

    @Serial
    private static final long serialVersionUID = 1L;

    public SearchIndexerNotFoundException(final String targetType) {
        super(SearchErrorCode.SEARCH_INDEXER_NOT_FOUND, "No SearchIndexer registered for targetType=" + targetType);
    }

}
