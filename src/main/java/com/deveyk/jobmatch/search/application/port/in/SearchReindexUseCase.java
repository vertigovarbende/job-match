package com.deveyk.jobmatch.search.application.port.in;

public interface SearchReindexUseCase {

    ReindexResult reindex(String targetType);

}
