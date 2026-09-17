package com.deveyk.jobmatch.candidate.presentation.rest;

import com.deveyk.jobmatch.candidate.application.CurrentCandidateFacade;
import com.deveyk.jobmatch.candidate.application.port.in.CandidateSkillUseCase;
import com.deveyk.jobmatch.candidate.domain.model.CandidateSkill;
import com.deveyk.jobmatch.candidate.presentation.rest.mapper.CandidateSkillRequestMapper;
import com.deveyk.jobmatch.candidate.presentation.rest.mapper.CandidateSkillResponseMapper;
import com.deveyk.jobmatch.candidate.presentation.rest.request.AttachCandidateSkillRequest;
import com.deveyk.jobmatch.candidate.presentation.rest.request.UpdateCandidateSkillRequest;
import com.deveyk.jobmatch.candidate.presentation.rest.response.CandidateSkillResponse;
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
public class CandidateSkillController {

    private final CandidateSkillUseCase candidateSkillUseCase;
    private final CandidateSkillRequestMapper candidateSkillRequestMapper;
    private final CandidateSkillResponseMapper candidateSkillResponseMapper;
    private final CurrentCandidateFacade currentCandidateFacade;

    @GetMapping(CandidateApiPaths.Skills.BASE)
    public BaseResponse<List<CandidateSkillResponse>> listSkills() {

        final Long candidateId = this.currentCandidateFacade.resolveCurrentCandidate().getId();
        final List<CandidateSkill> candidateSkills = this.candidateSkillUseCase.listSkills(candidateId);

        return BaseResponse.success(this.candidateSkillResponseMapper.toResponseList(candidateSkills));
    }

    @PostMapping(CandidateApiPaths.Skills.BASE)
    public BaseResponse<CandidateSkillResponse> attachSkill(@Valid @RequestBody final AttachCandidateSkillRequest request) {

        final Long candidateId = this.currentCandidateFacade.resolveCurrentCandidate().getId();
        final CandidateSkill candidateSkill = this.candidateSkillUseCase.attachSkill(this.candidateSkillRequestMapper.toCommand(request, candidateId));

        return BaseResponse.success(this.candidateSkillResponseMapper.toResponse(candidateSkill));
    }

    @PutMapping(CandidateApiPaths.Skills.BY_SKILL_ID)
    public BaseResponse<CandidateSkillResponse> updateSkillProficiency(@PathVariable final Long skillId, @Valid @RequestBody final UpdateCandidateSkillRequest request) {

        final Long candidateId = this.currentCandidateFacade.resolveCurrentCandidate().getId();
        final CandidateSkill candidateSkill = this.candidateSkillUseCase.updateSkillProficiency(this.candidateSkillRequestMapper.toCommand(request, candidateId, skillId));

        return BaseResponse.success(this.candidateSkillResponseMapper.toResponse(candidateSkill));
    }

    @DeleteMapping(CandidateApiPaths.Skills.BY_SKILL_ID)
    public BaseResponse<Void> detachSkill(@PathVariable final Long skillId) {

        final Long candidateId = this.currentCandidateFacade.resolveCurrentCandidate().getId();
        this.candidateSkillUseCase.detachSkill(candidateId, skillId);

        return BaseResponse.success();
    }

}
