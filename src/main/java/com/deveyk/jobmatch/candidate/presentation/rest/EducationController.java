package com.deveyk.jobmatch.candidate.presentation.rest;

import com.deveyk.jobmatch.candidate.application.CurrentCandidateFacade;
import com.deveyk.jobmatch.candidate.application.port.in.EducationUseCase;
import com.deveyk.jobmatch.candidate.domain.model.Education;
import com.deveyk.jobmatch.candidate.presentation.rest.mapper.EducationRequestMapper;
import com.deveyk.jobmatch.candidate.presentation.rest.mapper.EducationResponseMapper;
import com.deveyk.jobmatch.candidate.presentation.rest.request.AddEducationRequest;
import com.deveyk.jobmatch.candidate.presentation.rest.request.UpdateEducationRequest;
import com.deveyk.jobmatch.candidate.presentation.rest.response.EducationResponse;
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
public class EducationController {

    private final EducationUseCase educationUseCase;
    private final EducationRequestMapper educationRequestMapper;
    private final EducationResponseMapper educationResponseMapper;
    private final CurrentCandidateFacade currentCandidateFacade;

    @GetMapping(CandidateApiPaths.Educations.BASE)
    public BaseResponse<List<EducationResponse>> listEducations() {

        final Long candidateId = this.currentCandidateFacade.resolveCurrentCandidate().getId();
        final List<Education> educations = this.educationUseCase.listEducations(candidateId);

        return BaseResponse.success(this.educationResponseMapper.toResponseList(educations));
    }

    @PostMapping(CandidateApiPaths.Educations.BASE)
    public BaseResponse<EducationResponse> addEducation(@Valid @RequestBody final AddEducationRequest request) {

        final Long candidateId = this.currentCandidateFacade.resolveCurrentCandidate().getId();
        final Education education = this.educationUseCase.addEducation(this.educationRequestMapper.toCommand(request, candidateId));

        return BaseResponse.success(this.educationResponseMapper.toResponse(education));
    }

    @PutMapping(CandidateApiPaths.Educations.BY_EDUCATION_ID)
    public BaseResponse<EducationResponse> updateEducation(@PathVariable final Long educationId, @Valid @RequestBody final UpdateEducationRequest request) {

        final Long candidateId = this.currentCandidateFacade.resolveCurrentCandidate().getId();
        final Education education = this.educationUseCase.updateEducation(this.educationRequestMapper.toCommand(request, candidateId, educationId));

        return BaseResponse.success(this.educationResponseMapper.toResponse(education));
    }

    @DeleteMapping(CandidateApiPaths.Educations.BY_EDUCATION_ID)
    public BaseResponse<Void> deleteEducation(@PathVariable final Long educationId) {

        final Long candidateId = this.currentCandidateFacade.resolveCurrentCandidate().getId();
        this.educationUseCase.deleteEducation(candidateId, educationId);

        return BaseResponse.success();
    }

}
