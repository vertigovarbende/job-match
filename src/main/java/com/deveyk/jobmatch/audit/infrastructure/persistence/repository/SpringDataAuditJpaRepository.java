package com.deveyk.jobmatch.audit.infrastructure.persistence.repository;

import com.deveyk.jobmatch.audit.infrastructure.persistence.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataAuditJpaRepository extends JpaRepository<AuditLogEntity, Long> {


}
