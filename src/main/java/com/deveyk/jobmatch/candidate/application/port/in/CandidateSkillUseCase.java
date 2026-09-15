
package com.deveyk.jobmatch.candidate.application.port.in;

import com.deveyk.jobmatch.candidate.application.port.in.command.AttachCandidateSkillCommand;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateCandidateSkillCommand;
import com.deveyk.jobmatch.candidate.domain.model.CandidateSkill;

import java.util.List;

public interface CandidateSkillUseCase {

    CandidateSkill attachSkill(AttachCandidateSkillCommand command);

    CandidateSkill updateSkillProficiency(UpdateCandidateSkillCommand command);

    void detachSkill(Long candidateId, Long skillId);

    List<CandidateSkill> listSkills(Long candidateId);

}