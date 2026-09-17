package com.deveyk.jobmatch.catalog.application.service;

import com.deveyk.jobmatch.catalog.application.port.in.SkillUseCase;
import com.deveyk.jobmatch.catalog.application.port.out.SkillRepository;
import com.deveyk.jobmatch.catalog.domain.model.Skill;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService implements SkillUseCase {

    private final SkillRepository skillRepository;

    @Override
    @Transactional(readOnly = true)
    public JmPage<Skill> listSkills(final String name, final Pageable pageable) {

        log.debug("Listing skills: name={}, pageable={}", name, pageable);

        return this.skillRepository.findAll(name, pageable);
    }

}
