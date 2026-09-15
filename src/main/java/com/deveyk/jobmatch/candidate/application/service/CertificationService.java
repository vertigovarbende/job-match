// CertificationService.java
package com.deveyk.jobmatch.candidate.application.service;

import com.deveyk.jobmatch.candidate.application.port.in.command.AddCertificationCommand;
import com.deveyk.jobmatch.candidate.application.port.in.CertificationUseCase;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateCertificationCommand;
import com.deveyk.jobmatch.candidate.application.port.out.CertificationRepository;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateResourceForbiddenException;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateResourceNotFoundException;
import com.deveyk.jobmatch.candidate.domain.model.Certification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificationService implements CertificationUseCase {

    private static final String RESOURCE_TYPE = "Certification";

    private final CertificationRepository certificationRepository;

    @Override
    @Transactional
    public Certification addCertification(final AddCertificationCommand command) {

        log.debug("Adding certification: candidateId={}, name={}", command.candidateId(), command.name());

        final Certification certification = Certification.create(
                command.candidateId(),
                command.name(),
                command.issuingOrganization(),
                command.issueDate(),
                command.expiryDate(),
                command.credentialId(),
                command.credentialUrl(),
                command.description()
        );

        final Certification saved = this.certificationRepository.save(certification);

        log.info("Certification added: certificationId={}, candidateId={}", saved.getId(), saved.getCandidateId());

        return saved;
    }

    @Override
    @Transactional
    public Certification updateCertification(final UpdateCertificationCommand command) {

        log.debug("Updating certification: candidateId={}, certificationId={}", command.candidateId(), command.certificationId());

        final Certification certification = this.findByIdOrThrow(command.certificationId());
        this.checkOwnershipOrThrow(certification, command.candidateId());

        certification.update(
                command.name(),
                command.issuingOrganization(),
                command.issueDate(),
                command.expiryDate(),
                command.credentialId(),
                command.credentialUrl(),
                command.description()
        );

        final Certification saved = this.certificationRepository.save(certification);

        log.info("Certification updated: certificationId={}, candidateId={}", saved.getId(), saved.getCandidateId());

        return saved;
    }

    @Override
    @Transactional
    public void deleteCertification(final Long candidateId, final Long certificationId) {

        log.debug("Deleting certification: candidateId={}, certificationId={}", candidateId, certificationId);

        final Certification certification = this.findByIdOrThrow(certificationId);
        this.checkOwnershipOrThrow(certification, candidateId);

        this.certificationRepository.deleteById(certificationId);

        log.info("Certification deleted: certificationId={}, candidateId={}", certificationId, candidateId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Certification> listCertifications(final Long candidateId) {

        log.debug("Listing certifications: candidateId={}", candidateId);

        return this.certificationRepository.findAllByCandidateId(candidateId);
    }

    private Certification findByIdOrThrow(final Long certificationId) {
        return this.certificationRepository.findById(certificationId)
                .orElseThrow(() -> new CandidateResourceNotFoundException(RESOURCE_TYPE, certificationId));
    }

    private void checkOwnershipOrThrow(final Certification certification, final Long candidateId) {
        if (!certification.isOwnedBy(candidateId)) {
            throw new CandidateResourceForbiddenException(RESOURCE_TYPE, certification.getId(), candidateId);
        }
    }

}