package com.deveyk.jobmatch.catalog.application.port.in;

import com.deveyk.jobmatch.catalog.domain.model.Language;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import org.springframework.data.domain.Pageable;

public interface LanguageUseCase {

    JmPage<Language> listLanguages(String name, Pageable pageable);

}
