package com.deveyk.jobmatch.candidate.presentation.rest;

import com.deveyk.jobmatch.candidate.application.CurrentCandidateFacade;
import com.deveyk.jobmatch.candidate.application.port.in.ExperienceUseCase;
import com.deveyk.jobmatch.candidate.domain.model.Experience;
import com.deveyk.jobmatch.candidate.presentation.rest.mapper.ExperienceRequestMapper;
import com.deveyk.jobmatch.candidate.presentation.rest.mapper.ExperienceResponseMapper;
import com.deveyk.jobmatch.candidate.presentation.rest.request.AddExperienceRequest;
import com.deveyk.jobmatch.candidate.presentation.rest.request.UpdateExperienceRequest;
import com.deveyk.jobmatch.candidate.presentation.rest.response.ExperienceResponse;
import com.deveyk.jobmatch.shared.presentation.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceUseCase experienceUseCase;
    private final ExperienceRequestMapper experienceRequestMapper;
    private final ExperienceResponseMapper experienceResponseMapper;
    private final CurrentCandidateFacade currentCandidateFacade;

    @GetMapping(CandidateApiPaths.Experiences.BASE)
    public BaseResponse<List<ExperienceResponse>> listExperiences() {

        final Long candidateId = this.currentCandidateFacade.resolveCurrentCandidate().getId();
        final List<Experience> experiences = this.experienceUseCase.listExperiences(candidateId);

        return BaseResponse.success(this.experienceResponseMapper.toResponseList(experiences));
    }

    @PostMapping(CandidateApiPaths.Experiences.BASE)
    public BaseResponse<ExperienceResponse> addExperience(@Valid @RequestBody final AddExperienceRequest request) {

        final Long candidateId = this.currentCandidateFacade.resolveCurrentCandidate().getId();
        final Experience experience = this.experienceUseCase.addExperience(this.experienceRequestMapper.toCommand(request, candidateId));

        return BaseResponse.success(this.experienceResponseMapper.toResponse(experience));
    }

    @PutMapping(CandidateApiPaths.Experiences.BY_EXPERIENCE_ID)
    public BaseResponse<ExperienceResponse> updateExperience(@PathVariable final Long experienceId, @Valid @RequestBody final UpdateExperienceRequest request) {

        final Long candidateId = this.currentCandidateFacade.resolveCurrentCandidate().getId();
        final Experience experience = this.experienceUseCase.updateExperience(this.experienceRequestMapper.toCommand(request, candidateId, experienceId));

        return BaseResponse.success(this.experienceResponseMapper.toResponse(experience));
    }

    @DeleteMapping(CandidateApiPaths.Experiences.BY_EXPERIENCE_ID)
    public BaseResponse<Void> deleteExperience(@PathVariable final Long experienceId) {

        final Long candidateId = this.currentCandidateFacade.resolveCurrentCandidate().getId();
        this.experienceUseCase.deleteExperience(candidateId, experienceId);

        return BaseResponse.success();
    }

}
