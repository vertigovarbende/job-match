// CertificationPersistenceMapper.java
package com.deveyk.jobmatch.candidate.infrastructure.persistence.mapper;

import com.deveyk.jobmatch.candidate.domain.model.Certification;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.entity.CertificationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CertificationPersistenceMapper {

    CertificationEntity toEntity(Certification certification);

    Certification toDomain(CertificationEntity entity);

}