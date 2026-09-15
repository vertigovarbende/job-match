package com.deveyk.jobmatch.catalog.application.port.out;

import com.deveyk.jobmatch.catalog.domain.model.Language;

import java.util.List;

public interface LanguageRepository {

    List<Language> findAll();

}