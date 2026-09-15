// CandidateSkillPersistenceMapper.java
package com.deveyk.jobmatch.candidate.infrastructure.persistence.mapper;

import com.deveyk.jobmatch.candidate.domain.model.CandidateSkill;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.entity.CandidateSkillEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateSkillPersistenceMapper {

    CandidateSkillEntity toEntity(CandidateSkill candidateSkill);

    CandidateSkill toDomain(CandidateSkillEntity entity);

}