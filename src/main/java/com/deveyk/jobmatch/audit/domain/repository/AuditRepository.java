package com.deveyk.jobmatch.audit.domain.repository;

import com.deveyk.jobmatch.audit.domain.model.AuditLog;

public interface AuditRepository {

    void save(AuditLog auditLog);

}
