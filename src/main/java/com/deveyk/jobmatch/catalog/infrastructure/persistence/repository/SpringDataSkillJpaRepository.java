package com.deveyk.jobmatch.catalog.infrastructure.persistence.repository;

import com.deveyk.jobmatch.catalog.infrastructure.persistence.entity.SkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SpringDataSkillJpaRepository extends JpaRepository<SkillEntity, Long>, JpaSpecificationExecutor<SkillEntity> {

}
