package com.deveyk.jobmatch.candidate.application.port.in;

import com.deveyk.jobmatch.candidate.application.port.in.command.CreateCandidateProfileCommand;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateCandidateProfileCommand;
import com.deveyk.jobmatch.candidate.domain.model.Candidate;

public interface CandidateProfileUseCase {

    Candidate createProfile(CreateCandidateProfileCommand command);

    Candidate updateProfile(UpdateCandidateProfileCommand command);

    Candidate getProfile(Long userId);

}