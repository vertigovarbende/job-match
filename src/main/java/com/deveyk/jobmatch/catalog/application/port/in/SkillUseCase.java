package com.deveyk.jobmatch.catalog.application.port.in;

import com.deveyk.jobmatch.catalog.domain.model.Skill;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import org.springframework.data.domain.Pageable;

public interface SkillUseCase {

    JmPage<Skill> listSkills(String name, Pageable pageable);

}
