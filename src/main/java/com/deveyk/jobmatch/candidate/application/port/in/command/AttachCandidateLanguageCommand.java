
package com.deveyk.jobmatch.candidate.application.port.in.command;

import com.deveyk.jobmatch.candidate.domain.ProficiencyLevel;

public record AttachCandidateLanguageCommand(
        Long candidateId,
        Long languageId,
        ProficiencyLevel proficiencyLevel
) {

}