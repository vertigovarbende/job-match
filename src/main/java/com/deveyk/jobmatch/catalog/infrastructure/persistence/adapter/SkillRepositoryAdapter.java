package com.deveyk.jobmatch.catalog.infrastructure.persistence.adapter;

import com.deveyk.jobmatch.catalog.application.port.out.SkillRepository;
import com.deveyk.jobmatch.catalog.domain.model.Skill;
import com.deveyk.jobmatch.catalog.infrastructure.persistence.entity.SkillEntity;
import com.deveyk.jobmatch.catalog.infrastructure.persistence.filter.SkillEntityFilter;
import com.deveyk.jobmatch.catalog.infrastructure.persistence.mapper.SkillPersistenceMapper;
import com.deveyk.jobmatch.catalog.infrastructure.persistence.repository.SpringDataSkillJpaRepository;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SkillRepositoryAdapter implements SkillRepository {

    private final SpringDataSkillJpaRepository springDataSkillJpaRepository;
    private final SkillPersistenceMapper skillPersistenceMapper;

    @Override
    public JmPage<Skill> findAll(final String name, final Pageable pageable) {

        final SkillEntityFilter filter = SkillEntityFilter.builder()
                .name(name)
                .build();

        final Page<SkillEntity> page = this.springDataSkillJpaRepository.findAll(filter.toSpecification(), pageable);

        final var content = page.getContent().stream()
                .map(this.skillPersistenceMapper::toDomain)
                .toList();

        return JmPage.of(filter, page, content);
    }

}
