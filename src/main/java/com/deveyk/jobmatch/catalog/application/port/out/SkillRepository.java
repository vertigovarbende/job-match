package com.deveyk.jobmatch.catalog.application.port.out;

import com.deveyk.jobmatch.catalog.domain.model.Skill;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import org.springframework.data.domain.Pageable;

public interface SkillRepository {

    JmPage<Skill> findAll(String name, Pageable pageable);

}
