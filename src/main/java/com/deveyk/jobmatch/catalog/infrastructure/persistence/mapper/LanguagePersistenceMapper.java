// LanguagePersistenceMapper.java
package com.deveyk.jobmatch.catalog.infrastructure.persistence.mapper;

import com.deveyk.jobmatch.catalog.domain.model.Language;
import com.deveyk.jobmatch.catalog.infrastructure.persistence.entity.LanguageEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LanguagePersistenceMapper {

    LanguageEntity toEntity(Language language);

    Language toDomain(LanguageEntity entity);

}