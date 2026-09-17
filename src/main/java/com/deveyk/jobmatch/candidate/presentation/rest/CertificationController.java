package com.deveyk.jobmatch.candidate.presentation.rest;

import com.deveyk.jobmatch.candidate.application.CurrentCandidateFacade;
import com.deveyk.jobmatch.candidate.application.port.in.CertificationUseCase;
import com.deveyk.jobmatch.candidate.domain.model.Certification;
import com.deveyk.jobmatch.candidate.presentation.rest.mapper.CertificationRequestMapper;
import com.deveyk.jobmatch.candidate.presentation.rest.mapper.CertificationResponseMapper;
import com.deveyk.jobmatch.candidate.presentation.rest.request.AddCertificationRequest;
import com.deveyk.jobmatch.candidate.presentation.rest.request.UpdateCertificationRequest;
import com.deveyk.jobmatch.candidate.presentation.rest.response.CertificationResponse;
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
public class CertificationController {

    private final CertificationUseCase certificationUseCase;
    private final CertificationRequestMapper certificationRequestMapper;
    private final CertificationResponseMapper certificationResponseMapper;
    private final CurrentCandidateFacade currentCandidateFacade;

    @GetMapping(CandidateApiPaths.Certifications.BASE)
    public BaseResponse<List<CertificationResponse>> listCertifications() {

        final Long candidateId = this.currentCandidateFacade.resolveCurrentCandidate().getId();
        final List<Certification> certifications = this.certificationUseCase.listCertifications(candidateId);

        return BaseResponse.success(this.certificationResponseMapper.toResponseList(certifications));
    }

    @PostMapping(CandidateApiPaths.Certifications.BASE)
    public BaseResponse<CertificationResponse> addCertification(@Valid @RequestBody final AddCertificationRequest request) {

        final Long candidateId = this.currentCandidateFacade.resolveCurrentCandidate().getId();
        final Certification certification = this.certificationUseCase.addCertification(this.certificationRequestMapper.toCommand(request, candidateId));

        return BaseResponse.success(this.certificationResponseMapper.toResponse(certification));
    }

    @PutMapping(CandidateApiPaths.Certifications.BY_CERTIFICATION_ID)
    public BaseResponse<CertificationResponse> updateCertification(@PathVariable final Long certificationId, @Valid @RequestBody final UpdateCertificationRequest request) {

        final Long candidateId = this.currentCandidateFacade.resolveCurrentCandidate().getId();
        final Certification certification = this.certificationUseCase.updateCertification(this.certificationRequestMapper.toCommand(request, candidateId, certificationId));

        return BaseResponse.success(this.certificationResponseMapper.toResponse(certification));
    }

    @DeleteMapping(CandidateApiPaths.Certifications.BY_CERTIFICATION_ID)
    public BaseResponse<Void> deleteCertification(@PathVariable final Long certificationId) {

        final Long candidateId = this.currentCandidateFacade.resolveCurrentCandidate().getId();
        this.certificationUseCase.deleteCertification(candidateId, certificationId);

        return BaseResponse.success();
    }

}
