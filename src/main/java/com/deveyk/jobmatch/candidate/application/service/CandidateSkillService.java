// CandidateSkillService.java
package com.deveyk.jobmatch.candidate.application.service;

import com.deveyk.jobmatch.candidate.application.port.in.command.AttachCandidateSkillCommand;
import com.deveyk.jobmatch.candidate.application.port.in.CandidateSkillUseCase;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateCandidateSkillCommand;
import com.deveyk.jobmatch.candidate.application.port.out.CandidateSkillRepository;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateResourceNotFoundException;
import com.deveyk.jobmatch.candidate.domain.model.CandidateSkill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateSkillService implements CandidateSkillUseCase {

    private static final String RESOURCE_TYPE = "CandidateSkill";

    private final CandidateSkillRepository candidateSkillRepository;

    @Override
    @Transactional
    public CandidateSkill attachSkill(final AttachCandidateSkillCommand command) {

        log.debug("Attaching skill: candidateId={}, skillId={}", command.candidateId(), command.skillId());

        final CandidateSkill candidateSkill = CandidateSkill.create(
                command.candidateId(),
                command.skillId(),
                command.proficiencyLevel()
        );

        final CandidateSkill saved = this.candidateSkillRepository.save(candidateSkill);

        log.info("Skill attached: candidateId={}, skillId={}", saved.getCandidateId(), saved.getSkillId());

        return saved;
    }

    @Override
    @Transactional
    public CandidateSkill updateSkillProficiency(final UpdateCandidateSkillCommand command) {

        log.debug("Updating skill proficiency: candidateId={}, skillId={}", command.candidateId(), command.skillId());

        final CandidateSkill candidateSkill = this.findOrThrow(command.candidateId(), command.skillId());
        candidateSkill.updateProficiency(command.proficiencyLevel());

        final CandidateSkill saved = this.candidateSkillRepository.save(candidateSkill);

        log.info("Skill proficiency updated: candidateId={}, skillId={}", saved.getCandidateId(), saved.getSkillId());

        return saved;
    }

    @Override
    @Transactional
    public void detachSkill(final Long candidateId, final Long skillId) {

        log.debug("Detaching skill: candidateId={}, skillId={}", candidateId, skillId);

        this.findOrThrow(candidateId, skillId);
        this.candidateSkillRepository.deleteByCandidateIdAndSkillId(candidateId, skillId);

        log.info("Skill detached: candidateId={}, skillId={}", candidateId, skillId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CandidateSkill> listSkills(final Long candidateId) {

        log.debug("Listing skills: candidateId={}", candidateId);

        return this.candidateSkillRepository.findAllByCandidateId(candidateId);
    }

    private CandidateSkill findOrThrow(final Long candidateId, final Long skillId) {
        return this.candidateSkillRepository.findByCandidateIdAndSkillId(candidateId, skillId)
                .orElseThrow(() -> new CandidateResourceNotFoundException(RESOURCE_TYPE, skillId));
    }

}