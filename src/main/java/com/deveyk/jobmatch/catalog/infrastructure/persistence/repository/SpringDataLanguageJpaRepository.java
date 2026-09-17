package com.deveyk.jobmatch.catalog.infrastructure.persistence.repository;

import com.deveyk.jobmatch.catalog.infrastructure.persistence.entity.LanguageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SpringDataLanguageJpaRepository extends JpaRepository<LanguageEntity, Long>, JpaSpecificationExecutor<LanguageEntity> {

}
