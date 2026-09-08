package com.deveyk.jobmatch.audit.infrastructure.messaging.adapter;

import com.deveyk.jobmatch.audit.domain.event.AuditableDomainEvent;
import com.deveyk.jobmatch.audit.domain.model.AuditLog;
import com.deveyk.jobmatch.audit.domain.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * {@link AuditableDomainEvent} implemente eden HERHANGİ bir domain event'i otomatik olarak yakalayıp
 * {@code jm_audit_log}'a yazan generic listener (bkz. docs/AUDIT.md — "Generic Listener").
 * <p>
 * Yeni bir aksiyonu audit'e eklemek bu sınıfa dokunmayı gerektirmez; yalnızca ilgili event sınıfı
 * {@link AuditableDomainEvent}'i implemente eder.
 * <p>
 * Şimdilik in-process bir Spring application event mekanizması kullanılıyor (Kafka değil) — bu yüzden
 * {@code infrastructure.messaging} altında ama henüz bir "teknoloji" alt paketi (ör. {@code kafka})
 * olmadan duruyor. İleride Kafka eklendiğinde bu paket, Kafka publisher/consumer adapter'larıyla
 * yan yana yaşayacak; audit'in event kaynağı (bu listener'ın dinlediği {@link AuditableDomainEvent})
 * değişmeyecek, yalnızca event'in NASIL taşındığı (in-process vs. Kafka) infrastructure detayı olarak
 * kalacak.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditEventListener {

    private final AuditRepository auditRepository;

    /**
     * {@code phase = BEFORE_COMMIT}: audit kaydı, business transaction'ın İÇİNDE, aynı commit'te
     * yazılır (bkz. docs/AUDIT.md — "Transaction Zamanlaması"; {@code outbox_events} ile aynı
     * yaklaşım). Bunu elle belirtmek şart — {@code @TransactionalEventListener}'ın varsayılan
     * phase'i {@code AFTER_COMMIT}'tir, bu bizim tasarımımızla uyuşmaz.
     * <p>
     * {@code fallbackExecution = true}: event, aktif bir transaction olmadan publish edilirse
     * (ör. ileride bir geliştirici yanlışlıkla @Transactional olmayan bir yerden event fırlatırsa),
     * audit kaydı sessizce kaybolmak yerine yine de (transaction dışı olarak) yazılır. Audit
     * kaybının, atomiklik garantisinin küçük bir esnemesinden daha kötü bir sonuç olduğu
     * değerlendirilmiştir.
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, fallbackExecution = true)
    public void onAuditableDomainEvent(AuditableDomainEvent event) {
        AuditLog auditLog = AuditLog.from(event);
        auditRepository.save(auditLog);
        log.debug("Audit event captured: action={}, targetType={}, targetId={}", auditLog.getAction().code(), auditLog.getTargetType(), auditLog.getTargetId());
    }

}
