package com.deveyk.jobmatch.catalog.application.service;

import com.deveyk.jobmatch.catalog.application.port.in.LanguageUseCase;
import com.deveyk.jobmatch.catalog.application.port.out.LanguageRepository;
import com.deveyk.jobmatch.catalog.domain.model.Language;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LanguageService implements LanguageUseCase {

    private final LanguageRepository languageRepository;

    @Override
    @Transactional(readOnly = true)
    public JmPage<Language> listLanguages(final String name, final Pageable pageable) {

        log.debug("Listing languages: name={}, pageable={}", name, pageable);

        return this.languageRepository.findAll(name, pageable);
    }

}
