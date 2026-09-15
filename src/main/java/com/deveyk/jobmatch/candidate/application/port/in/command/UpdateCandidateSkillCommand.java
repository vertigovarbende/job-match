
package com.deveyk.jobmatch.candidate.application.port.in.command;

import com.deveyk.jobmatch.candidate.domain.ProficiencyLevel;

public record UpdateCandidateSkillCommand(
        Long candidateId,
        Long skillId,
        ProficiencyLevel proficiencyLevel
) {

}