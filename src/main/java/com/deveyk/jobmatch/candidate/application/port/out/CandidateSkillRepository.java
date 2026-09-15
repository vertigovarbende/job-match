package com.deveyk.jobmatch.candidate.application.port.out;

import com.deveyk.jobmatch.candidate.domain.model.CandidateSkill;

import java.util.List;
import java.util.Optional;

public interface CandidateSkillRepository {

    Optional<CandidateSkill> findByCandidateIdAndSkillId(Long candidateId, Long skillId);

    List<CandidateSkill> findAllByCandidateId(Long candidateId);

    CandidateSkill save(CandidateSkill candidateSkill);

    void deleteByCandidateIdAndSkillId(Long candidateId, Long skillId);

}