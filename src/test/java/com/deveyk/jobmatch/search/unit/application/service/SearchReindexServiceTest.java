package com.deveyk.jobmatch.search.unit.application.service;

import ch.qos.logback.classic.Level;
import com.deveyk.jobmatch.search.application.port.in.ReindexResult;
import com.deveyk.jobmatch.search.application.service.SearchReindexService;
import com.deveyk.jobmatch.search.domain.exception.SearchIndexerNotFoundException;
import com.deveyk.jobmatch.search.domain.indexer.SearchIndexer;
import com.deveyk.jobmatch.testsupport.LogTrackerConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("SearchReindexService - Birim Testleri")
@ExtendWith(MockitoExtension.class)
class SearchReindexServiceTest extends LogTrackerConfiguration {

    private static final String TARGET_TYPE = "SAMPLE";
    private static final int BATCH_SIZE = SearchReindexService.BATCH_SIZE;

    @Mock
    private SearchIndexer<?> indexer;

    private SearchReindexService searchReindexService;

    @BeforeEach
    void setUp() {

        when(this.indexer.targetType()).thenReturn(TARGET_TYPE);

        this.searchReindexService = new SearchReindexService(List.of(this.indexer));

    }

    @Test
    @DisplayName("reindex() kayitli indexer yoksa SearchIndexerNotFoundException firlatir ve hicbir sey silmez")
    void reindex_throwsSearchIndexerNotFoundException_whenNoIndexerIsRegistered() {

        assertThatThrownBy(() -> this.searchReindexService.reindex("UNKNOWN"))
                .isInstanceOf(SearchIndexerNotFoundException.class)
                .hasMessageContaining("UNKNOWN");

        verify(this.indexer, never()).removeAll();

    }

    @Test
    @DisplayName("reindex() targetType'i buyuk/kucuk harf ayirt etmeden eslestirir")
    void reindex_matchesTargetTypeCaseInsensitively() {

        when(this.indexer.findIndexableTargetIds(0, BATCH_SIZE)).thenReturn(List.of());

        final ReindexResult result = this.searchReindexService.reindex("sample");

        assertThat(result).isEqualTo(new ReindexResult(TARGET_TYPE, 0, 0, 0));

    }

    @Test
    @DisplayName("reindex() once tum dokumanlari siler, sonra hedef id'leri okuyup tek tek indexler")
    void reindex_removesAllDocumentsBeforeIndexingEachTarget() {

        when(this.indexer.findIndexableTargetIds(0, BATCH_SIZE)).thenReturn(List.of("1", "2"));

        this.searchReindexService.reindex(TARGET_TYPE);

        final InOrder inOrder = inOrder(this.indexer);
        inOrder.verify(this.indexer).removeAll();
        inOrder.verify(this.indexer).findIndexableTargetIds(0, BATCH_SIZE);
        inOrder.verify(this.indexer).index("1");
        inOrder.verify(this.indexer).index("2");

    }

    @Test
    @DisplayName("reindex() tum sayfalari gezer, sayfa BATCH_SIZE'tan kisa gelince durur ve sonucu sayar")
    void reindex_indexesEveryPageAndStopsWhenPageIsShorterThanBatchSize() {

        final List<String> fullPage = IntStream.rangeClosed(1, BATCH_SIZE).mapToObj(String::valueOf).toList();
        final List<String> shortPage = List.of("9001", "9002");

        when(this.indexer.findIndexableTargetIds(0, BATCH_SIZE)).thenReturn(fullPage);
        when(this.indexer.findIndexableTargetIds(1, BATCH_SIZE)).thenReturn(shortPage);

        final ReindexResult result = this.searchReindexService.reindex(TARGET_TYPE);

        assertThat(result).isEqualTo(new ReindexResult(TARGET_TYPE, BATCH_SIZE + 2, BATCH_SIZE + 2, 0));
        verify(this.indexer, times(BATCH_SIZE + 2)).index(anyString());
        verify(this.indexer, never()).findIndexableTargetIds(2, BATCH_SIZE);

    }

    @Test
    @DisplayName("reindex() tek bir hedef indexlenemese de devam eder, hatayi sayar ve baglamiyla loglar")
    void reindex_continuesAndCountsFailure_whenIndexingOneTargetFails() {

        when(this.indexer.findIndexableTargetIds(0, BATCH_SIZE)).thenReturn(List.of("1", "2", "3"));
        doNothing().when(this.indexer).index("1");
        doThrow(new IllegalStateException("elasticsearch unavailable")).when(this.indexer).index("2");
        doNothing().when(this.indexer).index("3");

        final ReindexResult result = this.searchReindexService.reindex(TARGET_TYPE);

        assertThat(result).isEqualTo(new ReindexResult(TARGET_TYPE, 3, 2, 1));
        verify(this.indexer).index("3");
        assertThat(this.logTracker.findMessage(Level.ERROR, "targetType=SAMPLE, targetId=2")).isPresent();

    }

    @Test
    @DisplayName("reindex() dokumanlar silinemezse hicbir hedefi okumaya baslamaz ve hatayi yayar")
    void reindex_propagatesAndDoesNotStartIndexing_whenRemoveAllFails() {

        doThrow(new IllegalStateException("elasticsearch unavailable")).when(this.indexer).removeAll();

        assertThatThrownBy(() -> this.searchReindexService.reindex(TARGET_TYPE))
                .isInstanceOf(IllegalStateException.class);

        verify(this.indexer, never()).findIndexableTargetIds(anyInt(), anyInt());
        verify(this.indexer, never()).index(anyString());

    }

}
