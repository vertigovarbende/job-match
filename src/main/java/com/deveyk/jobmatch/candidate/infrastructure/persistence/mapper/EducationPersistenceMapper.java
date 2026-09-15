// EducationPersistenceMapper.java
package com.deveyk.jobmatch.candidate.infrastructure.persistence.mapper;

import com.deveyk.jobmatch.candidate.domain.model.Education;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.entity.EducationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EducationPersistenceMapper {

    EducationEntity toEntity(Education education);

    Education toDomain(EducationEntity entity);

}