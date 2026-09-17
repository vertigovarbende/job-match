package com.deveyk.jobmatch.catalog.infrastructure.persistence.adapter;

import com.deveyk.jobmatch.catalog.application.port.out.LanguageRepository;
import com.deveyk.jobmatch.catalog.domain.model.Language;
import com.deveyk.jobmatch.catalog.infrastructure.persistence.entity.LanguageEntity;
import com.deveyk.jobmatch.catalog.infrastructure.persistence.filter.LanguageEntityFilter;
import com.deveyk.jobmatch.catalog.infrastructure.persistence.mapper.LanguagePersistenceMapper;
import com.deveyk.jobmatch.catalog.infrastructure.persistence.repository.SpringDataLanguageJpaRepository;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LanguageRepositoryAdapter implements LanguageRepository {

    private final SpringDataLanguageJpaRepository springDataLanguageJpaRepository;
    private final LanguagePersistenceMapper languagePersistenceMapper;

    @Override
    public JmPage<Language> findAll(final String name, final Pageable pageable) {

        final LanguageEntityFilter filter = LanguageEntityFilter.builder()
                .name(name)
                .build();

        final Page<LanguageEntity> page = this.springDataLanguageJpaRepository.findAll(filter.toSpecification(), pageable);

        final var content = page.getContent().stream()
                .map(this.languagePersistenceMapper::toDomain)
                .toList();

        return JmPage.of(filter, page, content);
    }

}
