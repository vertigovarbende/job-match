package com.deveyk.jobmatch.search.unit.infrastructure.messaging.adapter;

import ch.qos.logback.classic.Level;
import com.deveyk.jobmatch.search.domain.indexer.SearchIndexer;
import com.deveyk.jobmatch.search.infrastructure.messaging.adapter.SearchIndexEventListener;
import com.deveyk.jobmatch.testsupport.LogTrackerConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.deveyk.jobmatch.search.testsupport.SampleSearchIndexableEvent.delete;
import static com.deveyk.jobmatch.search.testsupport.SampleSearchIndexableEvent.upsert;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("SearchIndexEventListener - Birim Testleri")
class SearchIndexEventListenerTest extends LogTrackerConfiguration {

    private static final String TARGET_TYPE = "SAMPLE";
    private static final String TARGET_ID = "42";

    private final SearchIndexer<?> indexer = mock(SearchIndexer.class);

    private SearchIndexEventListener listener;

    @BeforeEach
    void setUp() {

        when(this.indexer.targetType()).thenReturn(TARGET_TYPE);

        this.listener = new SearchIndexEventListener(List.of(this.indexer));

    }

    @Test
    @DisplayName("onSearchIndexableEvent() UPSERT event'inde eslesen indexer'in index() metodunu cagirir")
    void onSearchIndexableEvent_callsIndex_whenOperationIsUpsert() {

        this.listener.onSearchIndexableEvent(upsert(TARGET_TYPE, TARGET_ID));

        verify(this.indexer).index(TARGET_ID);
        verify(this.indexer, never()).remove(anyString());

    }

    @Test
    @DisplayName("onSearchIndexableEvent() DELETE event'inde eslesen indexer'in remove() metodunu cagirir")
    void onSearchIndexableEvent_callsRemove_whenOperationIsDelete() {

        this.listener.onSearchIndexableEvent(delete(TARGET_TYPE, TARGET_ID));

        verify(this.indexer).remove(TARGET_ID);
        verify(this.indexer, never()).index(anyString());

    }

    @Test
    @DisplayName("onSearchIndexableEvent() kayitli indexer yoksa uyari loglar ve hicbir indexer'i cagirmaz")
    void onSearchIndexableEvent_logsWarning_whenNoIndexerIsRegisteredForTargetType() {

        this.listener.onSearchIndexableEvent(upsert("UNKNOWN", TARGET_ID));

        assertThat(this.logTracker.findMessage(Level.WARN, "targetType=UNKNOWN")).isPresent();
        verify(this.indexer, never()).index(anyString());
        verify(this.indexer, never()).remove(anyString());

    }

    @Test
    @DisplayName("onSearchIndexableEvent() UPSERT sirasinda indexer hata firlatirsa exception'i yaymaz ve hatayi baglamiyla loglar")
    void onSearchIndexableEvent_doesNotPropagateAndLogsError_whenIndexingFails() {

        doThrow(new IllegalStateException("elasticsearch unavailable")).when(this.indexer).index(TARGET_ID);

        assertThatCode(() -> this.listener.onSearchIndexableEvent(upsert(TARGET_TYPE, TARGET_ID)))
                .doesNotThrowAnyException();

        assertThat(this.logTracker.findMessage(Level.ERROR, "targetType=SAMPLE, targetId=42, operation=UPSERT")).isPresent();

    }

    @Test
    @DisplayName("onSearchIndexableEvent() DELETE sirasinda indexer hata firlatirsa exception'i yaymaz ve hatayi baglamiyla loglar")
    void onSearchIndexableEvent_doesNotPropagateAndLogsError_whenRemovalFails() {

        doThrow(new IllegalStateException("elasticsearch unavailable")).when(this.indexer).remove(TARGET_ID);

        assertThatCode(() -> this.listener.onSearchIndexableEvent(delete(TARGET_TYPE, TARGET_ID)))
                .doesNotThrowAnyException();

        assertThat(this.logTracker.findMessage(Level.ERROR, "targetType=SAMPLE, targetId=42, operation=DELETE")).isPresent();

    }

}
