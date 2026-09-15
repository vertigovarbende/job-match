package com.deveyk.jobmatch.catalog.infrastructure.persistence.repository;

import com.deveyk.jobmatch.catalog.infrastructure.persistence.entity.LanguageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataLanguageJpaRepository extends JpaRepository<LanguageEntity, Long> {


}