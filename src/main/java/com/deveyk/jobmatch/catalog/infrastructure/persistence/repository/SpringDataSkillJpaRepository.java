package com.deveyk.jobmatch.catalog.infrastructure.persistence.repository;

import com.deveyk.jobmatch.catalog.infrastructure.persistence.entity.SkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataSkillJpaRepository extends JpaRepository<SkillEntity, Long> {


}