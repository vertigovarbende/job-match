package com.deveyk.jobmatch.catalog.application.port.out;

import com.deveyk.jobmatch.catalog.domain.model.Language;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import org.springframework.data.domain.Pageable;

public interface LanguageRepository {

    JmPage<Language> findAll(String name, Pageable pageable);

}
