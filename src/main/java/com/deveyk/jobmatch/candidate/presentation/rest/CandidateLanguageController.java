package com.deveyk.jobmatch.candidate.presentation.rest;

import com.deveyk.jobmatch.candidate.application.CurrentCandidateFacade;
import com.deveyk.jobmatch.candidate.application.port.in.CandidateLanguageUseCase;
import com.deveyk.jobmatch.candidate.domain.model.CandidateLanguage;
import com.deveyk.jobmatch.candidate.presentation.rest.mapper.CandidateLanguageRequestMapper;
import com.deveyk.jobmatch.candidate.presentation.rest.mapper.CandidateLanguageResponseMapper;
import com.deveyk.jobmatch.candidate.presentation.rest.request.AttachCandidateLanguageRequest;
import com.deveyk.jobmatch.candidate.presentation.rest.request.UpdateCandidateLanguageRequest;
import com.deveyk.jobmatch.candidate.presentation.rest.response.CandidateLanguageResponse;
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
public class CandidateLanguageController {

    private final CandidateLanguageUseCase candidateLanguageUseCase;
    private final CandidateLanguageRequestMapper candidateLanguageRequestMapper;
    private final CandidateLanguageResponseMapper candidateLanguageResponseMapper;
    private final CurrentCandidateFacade currentCandidateFacade;

    @GetMapping(CandidateApiPaths.Languages.BASE)
    public BaseResponse<List<CandidateLanguageResponse>> listLanguages() {

        final Long candidateId = this.currentCandidateFacade.resolveCurrentCandidate().getId();
        final List<CandidateLanguage> candidateLanguages = this.candidateLanguageUseCase.listLanguages(candidateId);

        return BaseResponse.success(this.candidateLanguageResponseMapper.toResponseList(candidateLanguages));
    }

    @PostMapping(CandidateApiPaths.Languages.BASE)
    public BaseResponse<CandidateLanguageResponse> attachLanguage(@Valid @RequestBody final AttachCandidateLanguageRequest request) {

        final Long candidateId = this.currentCandidateFacade.resolveCurrentCandidate().getId();
        final CandidateLanguage candidateLanguage = this.candidateLanguageUseCase.attachLanguage(this.candidateLanguageRequestMapper.toCommand(request, candidateId));

        return BaseResponse.success(this.candidateLanguageResponseMapper.toResponse(candidateLanguage));
    }

    @PutMapping(CandidateApiPaths.Languages.BY_LANGUAGE_ID)
    public BaseResponse<CandidateLanguageResponse> updateLanguageProficiency(@PathVariable final Long languageId, @Valid @RequestBody final UpdateCandidateLanguageRequest request) {

        final Long candidateId = this.currentCandidateFacade.resolveCurrentCandidate().getId();
        final CandidateLanguage candidateLanguage = this.candidateLanguageUseCase.updateLanguageProficiency(this.candidateLanguageRequestMapper.toCommand(request, candidateId, languageId));

        return BaseResponse.success(this.candidateLanguageResponseMapper.toResponse(candidateLanguage));
    }

    @DeleteMapping(CandidateApiPaths.Languages.BY_LANGUAGE_ID)
    public BaseResponse<Void> detachLanguage(@PathVariable final Long languageId) {

        final Long candidateId = this.currentCandidateFacade.resolveCurrentCandidate().getId();
        this.candidateLanguageUseCase.detachLanguage(candidateId, languageId);

        return BaseResponse.success();
    }

}
