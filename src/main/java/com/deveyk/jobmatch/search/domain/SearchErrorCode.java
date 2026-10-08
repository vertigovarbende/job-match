package com.deveyk.jobmatch.search.domain;

import com.deveyk.jobmatch.shared.domain.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum SearchErrorCode implements ErrorCode {

    SEARCH_INDEXER_NOT_FOUND(
            "SRC_001",
            "SEARCH_INDEXER_NOT_FOUND",
            "No search indexer is registered for the given target type."
    );

    private final String code;
    private final String header;
    private final String defaultMessage;

}
