// SkillRepositoryAdapter.java
package com.deveyk.jobmatch.catalog.infrastructure.persistence.adapter;

import com.deveyk.jobmatch.catalog.application.port.out.SkillRepository;
import com.deveyk.jobmatch.catalog.domain.model.Skill;
import com.deveyk.jobmatch.catalog.infrastructure.persistence.mapper.SkillPersistenceMapper;
import com.deveyk.jobmatch.catalog.infrastructure.persistence.repository.SpringDataSkillJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillRepositoryAdapter implements SkillRepository {

    private final SpringDataSkillJpaRepository springDataSkillJpaRepository;
    private final SkillPersistenceMapper skillPersistenceMapper;

    @Override
    public List<Skill> findAll() {
        return this.springDataSkillJpaRepository.findAll().stream()
                .map(this.skillPersistenceMapper::toDomain)
                .toList();
    }

}