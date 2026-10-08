package com.deveyk.jobmatch.search.application.service;

import com.deveyk.jobmatch.search.application.port.in.ReindexResult;
import com.deveyk.jobmatch.search.application.port.in.SearchReindexUseCase;
import com.deveyk.jobmatch.search.domain.exception.SearchIndexerNotFoundException;
import com.deveyk.jobmatch.search.domain.indexer.SearchIndexer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchReindexService implements SearchReindexUseCase {

    public static final int BATCH_SIZE = 100;

    private final Map<String, SearchIndexer<?>> indexersByTargetType;

    public SearchReindexService(final List<SearchIndexer<?>> indexers) {
        this.indexersByTargetType = indexers.stream()
                .collect(Collectors.toMap(SearchIndexer::targetType, Function.identity()));
    }

    @Override
    public ReindexResult reindex(final String targetType) {

        final String normalizedTargetType = targetType.toUpperCase(Locale.ROOT);
        final SearchIndexer<?> indexer = this.indexersByTargetType.get(normalizedTargetType);

        if (indexer == null) {
            throw new SearchIndexerNotFoundException(targetType);
        }

        log.info("Search reindex started: targetType={}", normalizedTargetType);

        indexer.removeAll();

        int page = 0;
        int total = 0;
        int indexed = 0;
        int failed = 0;
        List<String> targetIds;

        do {

            targetIds = indexer.findIndexableTargetIds(page, BATCH_SIZE);

            for (final String targetId : targetIds) {

                total++;

                try {
                    indexer.index(targetId);
                    indexed++;
                } catch (final RuntimeException exception) {
                    failed++;
                    log.error("Search reindex failed for target: targetType={}, targetId={}", normalizedTargetType, targetId, exception);
                }

            }

            page++;

        } while (targetIds.size() >= BATCH_SIZE);

        log.info("Search reindex finished: targetType={}, total={}, indexed={}, failed={}", normalizedTargetType, total, indexed, failed);

        return ReindexResult.builder()
                .targetType(normalizedTargetType)
                .total(total)
                .indexed(indexed)
                .failed(failed)
                .build();

    }

}
