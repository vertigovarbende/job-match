package com.deveyk.jobmatch.search.domain.event;

public interface SearchIndexableEvent {

    String targetType();

    String targetId();

    IndexOperation operation();

}
