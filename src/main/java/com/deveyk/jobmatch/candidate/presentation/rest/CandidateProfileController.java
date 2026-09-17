package com.deveyk.jobmatch.candidate.presentation.rest;

import com.deveyk.jobmatch.candidate.application.port.in.CandidateProfileUseCase;
import com.deveyk.jobmatch.candidate.domain.model.Candidate;
import com.deveyk.jobmatch.candidate.presentation.rest.mapper.CandidateProfileRequestMapper;
import com.deveyk.jobmatch.candidate.presentation.rest.mapper.CandidateProfileResponseMapper;
import com.deveyk.jobmatch.candidate.presentation.rest.request.UpdateCandidateProfileRequest;
import com.deveyk.jobmatch.candidate.presentation.rest.response.CandidateResponse;
import com.deveyk.jobmatch.identity.application.CurrentUserFacade;
import com.deveyk.jobmatch.identity.domain.model.JmUser;
import com.deveyk.jobmatch.shared.presentation.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CandidateProfileController {

    private final CandidateProfileUseCase candidateProfileUseCase;
    private final CandidateProfileRequestMapper candidateProfileRequestMapper;
    private final CandidateProfileResponseMapper candidateProfileResponseMapper;
    private final CurrentUserFacade currentUserFacade;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(CandidateApiPaths.Profile.BASE)
    public BaseResponse<CandidateResponse> createProfile() {

        final JmUser currentUser = this.currentUserFacade.resolveCurrentUser();
        final Candidate candidate = this.candidateProfileUseCase.createProfile(this.candidateProfileRequestMapper.toCommand(currentUser.getId(), currentUser.getRole()));

        return BaseResponse.success(this.candidateProfileResponseMapper.toResponse(candidate));
    }

    @GetMapping(CandidateApiPaths.Profile.BASE)
    public BaseResponse<CandidateResponse> getProfile() {

        final JmUser currentUser = this.currentUserFacade.resolveCurrentUser();
        final Candidate candidate = this.candidateProfileUseCase.getProfile(currentUser.getId());

        return BaseResponse.success(this.candidateProfileResponseMapper.toResponse(candidate));
    }

    @PutMapping(CandidateApiPaths.Profile.BASE)
    public BaseResponse<CandidateResponse> updateProfile(@Valid @RequestBody final UpdateCandidateProfileRequest request) {

        final JmUser currentUser = this.currentUserFacade.resolveCurrentUser();
        final Candidate candidate = this.candidateProfileUseCase.updateProfile(this.candidateProfileRequestMapper.toCommand(request, currentUser.getId()));

        return BaseResponse.success(this.candidateProfileResponseMapper.toResponse(candidate));
    }

}
