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

/**
 * {@link SearchIndexableEvent} implemente eden HERHANGİ bir domain event'i otomatik olarak yakalayıp
 * {@link SearchIndexer#targetType()} eşlemesiyle doğru indexer'a delege eden generic listener
 * (bkz. docs/ELASTICSEARCH.md — Açık Kararlar madde 1, 3; {@link com.deveyk.jobmatch.audit.infrastructure.messaging.adapter.AuditEventListener}'ın
 * aynı Observer/generic-listener deseni).
 * <p>
 * Yeni bir aggregate'in arama indeksine bağlanması bu sınıfa dokunmayı gerektirmez; yalnızca ilgili
 * event sınıfı {@link SearchIndexableEvent}'i, ilgili infrastructure sınıfı da yeni bir
 * {@link SearchIndexer} implementasyonunu (kendi {@code targetType()}'ıyla) implemente eder ve Spring
 * bean'i olarak register edilir.
 */
@Component
@Slf4j
public class SearchIndexEventListener {

    private final Map<String, SearchIndexer<?>> indexersByTargetType;

    public SearchIndexEventListener(List<SearchIndexer<?>> indexers) {
        this.indexersByTargetType = indexers.stream()
                .collect(Collectors.toMap(SearchIndexer::targetType, Function.identity()));
    }

    /**
     * {@code phase = AFTER_COMMIT} (Spring'in varsayılanı, ama bilinçli olarak açıkça yazıldı):
     * {@link com.deveyk.jobmatch.audit.infrastructure.messaging.adapter.AuditEventListener}'ın aksine
     * -- audit kaydı PostgreSQL'e, business transaction'ıyla AYNI veri deposuna yazıldığı için
     * {@code BEFORE_COMMIT} güvenliydi (birlikte rollback olur). Elasticsearch ayrı bir veri deposu;
     * transaction sonradan rollback olursa ES'e önceden yazılmış bir doküman geri alınamaz. Bu yüzden
     * yalnızca transaction gerçekten commit olduktan SONRA indekslenir (bkz. docs/ELASTICSEARCH.md,
     * Açık Kararlar madde 1 — "Somut tasarım").
     * <p>
     * {@code fallbackExecution = true}: event aktif bir transaction olmadan publish edilirse yine de
     * (transaction dışı olarak, hemen) işlenir -- {@code AuditEventListener} ile aynı savunma amaçlı
     * gerekçe, burada arama indeksinin senkron kalmaması audit kaydının kaybolmasından daha az önemli
     * değil (ör. kapatılmış bir ilanın public aramada görünmeye devam etmesi).
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onSearchIndexableEvent(SearchIndexableEvent event) {

        SearchIndexer<?> indexer = indexersByTargetType.get(event.targetType());

        if (indexer == null) {
            log.warn("No SearchIndexer registered for targetType={}, targetId={}", event.targetType(), event.targetId());
            return;
        }

        switch (event.operation()) {
            case UPSERT -> indexer.index(event.targetId());
            case DELETE -> indexer.remove(event.targetId());
        }

        log.debug("Search index event handled: targetType={}, targetId={}, operation={}", event.targetType(), event.targetId(), event.operation());
    }

}
