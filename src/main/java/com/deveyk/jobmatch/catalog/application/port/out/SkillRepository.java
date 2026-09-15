package com.deveyk.jobmatch.catalog.application.port.out;

import com.deveyk.jobmatch.catalog.domain.model.Skill;

import java.util.List;

public interface SkillRepository {

    List<Skill> findAll();

}