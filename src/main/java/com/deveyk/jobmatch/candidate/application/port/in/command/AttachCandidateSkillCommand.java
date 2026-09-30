
package com.deveyk.jobmatch.candidate.application.port.in.command;

import com.deveyk.jobmatch.candidate.domain.ProficiencyLevel;
import lombok.Builder;

@Builder
public record AttachCandidateSkillCommand(
        Long candidateId,
        Long skillId,
        ProficiencyLevel proficiencyLevel
) {

}
