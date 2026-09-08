package com.deveyk.jobmatch.audit.infrastructure.persistence.mapper;

import com.deveyk.jobmatch.audit.domain.model.AuditLog;
import com.deveyk.jobmatch.audit.infrastructure.persistence.entity.AuditLogEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = AuditDetailsConverter.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AuditLogEntityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "action", expression = "java(auditLog.getAction().code())")
    AuditLogEntity toEntity(AuditLog auditLog);

}
