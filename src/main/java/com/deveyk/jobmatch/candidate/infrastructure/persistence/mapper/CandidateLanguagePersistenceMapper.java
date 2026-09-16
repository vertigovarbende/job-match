package com.deveyk.jobmatch.candidate.infrastructure.persistence.mapper;

import com.deveyk.jobmatch.candidate.domain.model.CandidateLanguage;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.entity.CandidateLanguageEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateLanguagePersistenceMapper {

    CandidateLanguageEntity toEntity(CandidateLanguage candidateLanguage);

    CandidateLanguage toDomain(CandidateLanguageEntity entity);

}