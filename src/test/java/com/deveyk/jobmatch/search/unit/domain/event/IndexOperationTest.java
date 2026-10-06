package com.deveyk.jobmatch.search.unit.domain.event;

import com.deveyk.jobmatch.search.domain.event.IndexOperation;
import com.deveyk.jobmatch.search.domain.indexer.SearchIndexer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DisplayName("IndexOperation - Birim Testleri")
class IndexOperationTest {

    private static final String TARGET_ID = "42";

    private final SearchIndexer<?> indexer = mock(SearchIndexer.class);

    @Test
    @DisplayName("applyTo() UPSERT icin indexer'in index() metodunu cagirir")
    void applyTo_callsIndex_whenOperationIsUpsert() {

        IndexOperation.UPSERT.applyTo(this.indexer, TARGET_ID);

        verify(this.indexer).index(TARGET_ID);
        verify(this.indexer, never()).remove(anyString());

    }

    @Test
    @DisplayName("applyTo() DELETE icin indexer'in remove() metodunu cagirir")
    void applyTo_callsRemove_whenOperationIsDelete() {

        IndexOperation.DELETE.applyTo(this.indexer, TARGET_ID);

        verify(this.indexer).remove(TARGET_ID);
        verify(this.indexer, never()).index(anyString());

    }

}
