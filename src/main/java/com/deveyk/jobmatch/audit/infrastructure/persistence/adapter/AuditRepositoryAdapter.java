package com.deveyk.jobmatch.audit.infrastructure.persistence.adapter;

import com.deveyk.jobmatch.audit.domain.model.AuditLog;
import com.deveyk.jobmatch.audit.domain.repository.AuditRepository;
import com.deveyk.jobmatch.audit.infrastructure.persistence.entity.AuditLogEntity;
import com.deveyk.jobmatch.audit.infrastructure.persistence.mapper.AuditLogEntityMapper;
import com.deveyk.jobmatch.audit.infrastructure.persistence.repository.SpringDataAuditJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditRepositoryAdapter implements AuditRepository {

    // --------- DEPENDENCY INJECTION VARIABLES ---------
    private final SpringDataAuditJpaRepository springDataAuditJpaRepository;
    private final AuditLogEntityMapper auditLogEntityMapper;

    @Override
    public void save(AuditLog auditLog) {
        AuditLogEntity entity = auditLogEntityMapper.toEntity(auditLog);
        springDataAuditJpaRepository.save(entity);
        log.debug("Audit log persisted: action={}, targetType={}, targetId={}", entity.getAction(), entity.getTargetType(), entity.getTargetId());
    }

}
