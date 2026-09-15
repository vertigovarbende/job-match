
package com.deveyk.jobmatch.candidate.application.port.in;

import com.deveyk.jobmatch.candidate.application.port.in.command.AddEducationCommand;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateEducationCommand;
import com.deveyk.jobmatch.candidate.domain.model.Education;

import java.util.List;

public interface EducationUseCase {

    Education addEducation(AddEducationCommand command);

    Education updateEducation(UpdateEducationCommand command);

    void deleteEducation(Long candidateId, Long educationId);

    List<Education> listEducations(Long candidateId);

}