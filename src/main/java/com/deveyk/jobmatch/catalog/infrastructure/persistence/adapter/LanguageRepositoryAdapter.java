// LanguageRepositoryAdapter.java
package com.deveyk.jobmatch.catalog.infrastructure.persistence.adapter;

import com.deveyk.jobmatch.catalog.application.port.out.LanguageRepository;
import com.deveyk.jobmatch.catalog.domain.model.Language;
import com.deveyk.jobmatch.catalog.infrastructure.persistence.mapper.LanguagePersistenceMapper;
import com.deveyk.jobmatch.catalog.infrastructure.persistence.repository.SpringDataLanguageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LanguageRepositoryAdapter implements LanguageRepository {

    private final SpringDataLanguageJpaRepository springDataLanguageJpaRepository;
    private final LanguagePersistenceMapper languagePersistenceMapper;

    @Override
    public List<Language> findAll() {
        return this.springDataLanguageJpaRepository.findAll().stream()
                .map(this.languagePersistenceMapper::toDomain)
                .toList();
    }

}