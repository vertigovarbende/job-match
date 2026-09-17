package com.deveyk.jobmatch.candidate.application;

import com.deveyk.jobmatch.candidate.application.port.in.CandidateProfileUseCase;
import com.deveyk.jobmatch.candidate.domain.model.Candidate;
import com.deveyk.jobmatch.identity.application.CurrentUserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentCandidateFacade {

    private final CurrentUserFacade currentUserFacade;
    private final CandidateProfileUseCase candidateProfileUseCase;

    public Candidate resolveCurrentCandidate() {

        final Long userId = this.currentUserFacade.resolveCurrentUser().getId();

        return this.candidateProfileUseCase.getProfile(userId);
    }

}
