
package com.deveyk.jobmatch.candidate.application.port.in;

import com.deveyk.jobmatch.candidate.application.port.in.command.AddExperienceCommand;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateExperienceCommand;
import com.deveyk.jobmatch.candidate.domain.model.Experience;

import java.util.List;

public interface ExperienceUseCase {

    Experience addExperience(AddExperienceCommand command);

    Experience updateExperience(UpdateExperienceCommand command);

    void deleteExperience(Long candidateId, Long experienceId);

    List<Experience> listExperiences(Long candidateId);

}