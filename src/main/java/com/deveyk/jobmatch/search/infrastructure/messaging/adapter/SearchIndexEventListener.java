package com.deveyk.jobmatch.search.infrastructure.messaging.adapter;

import com.deveyk.jobmatch.search.domain.event.SearchIndexableEvent;
import com.deveyk.jobmatch.search.domain.indexer.SearchIndexer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SearchIndexEventListener {

    private final Map<String, SearchIndexer<?>> indexersByTargetType;

    public SearchIndexEventListener(List<SearchIndexer<?>> indexers) {
        this.indexersByTargetType = indexers.stream()
                .collect(Collectors.toMap(SearchIndexer::targetType, Function.identity()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onSearchIndexableEvent(SearchIndexableEvent event) {

        SearchIndexer<?> indexer = indexersByTargetType.get(event.targetType());

        if (indexer == null) {
            log.warn("No SearchIndexer registered for targetType={}, targetId={}", event.targetType(), event.targetId());
            return;
        }

        try {
            event.operation().applyTo(indexer, event.targetId());
        } catch (RuntimeException e) {
            log.error("Search index operation failed: targetType={}, targetId={}, operation={}", event.targetType(), event.targetId(), event.operation(), e);
            return;
        }

        log.debug("Search index event handled: targetType={}, targetId={}, operation={}", event.targetType(), event.targetId(), event.operation());
    }

}
