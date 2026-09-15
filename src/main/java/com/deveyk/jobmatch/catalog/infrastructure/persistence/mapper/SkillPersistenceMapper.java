// SkillPersistenceMapper.java
package com.deveyk.jobmatch.catalog.infrastructure.persistence.mapper;

import com.deveyk.jobmatch.catalog.domain.model.Skill;
import com.deveyk.jobmatch.catalog.infrastructure.persistence.entity.SkillEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SkillPersistenceMapper {

    SkillEntity toEntity(Skill skill);

    Skill toDomain(SkillEntity entity);

}