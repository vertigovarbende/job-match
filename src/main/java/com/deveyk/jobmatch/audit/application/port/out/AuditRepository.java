package com.deveyk.jobmatch.audit.application.port.out;

import com.deveyk.jobmatch.audit.domain.model.AuditLog;

public interface AuditRepository {

    void save(AuditLog auditLog);

}
