package com.deveyk.jobmatch.candidate.application.service;

import com.deveyk.jobmatch.candidate.application.port.in.command.AddEducationCommand;
import com.deveyk.jobmatch.candidate.application.port.in.EducationUseCase;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateEducationCommand;
import com.deveyk.jobmatch.candidate.application.port.out.EducationRepository;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateResourceForbiddenException;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateResourceNotFoundException;
import com.deveyk.jobmatch.candidate.domain.model.Education;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EducationService implements EducationUseCase {

    private static final String RESOURCE_TYPE = "Education";

    private final EducationRepository educationRepository;

    @Override
    @Transactional
    public Education addEducation(final AddEducationCommand command) {

        log.debug("Adding education: candidateId={}, institution={}", command.candidateId(), command.institution());

        final Education education = Education.create(
                command.candidateId(),
                command.institution(),
                command.degree(),
                command.fieldOfStudy(),
                command.startDate(),
                command.endDate(),
                command.description()
        );

        final Education saved = this.educationRepository.save(education);

        log.info("Education added: educationId={}, candidateId={}", saved.getId(), saved.getCandidateId());

        return saved;
    }

    @Override
    @Transactional
    public Education updateEducation(final UpdateEducationCommand command) {

        log.debug("Updating education: candidateId={}, educationId={}", command.candidateId(), command.educationId());

        final Education education = this.findByIdOrThrow(command.educationId());
        this.checkOwnershipOrThrow(education, command.candidateId());

        education.update(
                command.institution(),
                command.degree(),
                command.fieldOfStudy(),
                command.startDate(),
                command.endDate(),
                command.description()
        );

        final Education saved = this.educationRepository.save(education);

        log.info("Education updated: educationId={}, candidateId={}", saved.getId(), saved.getCandidateId());

        return saved;
    }

    @Override
    @Transactional
    public void deleteEducation(final Long candidateId, final Long educationId) {

        log.debug("Deleting education: candidateId={}, educationId={}", candidateId, educationId);

        final Education education = this.findByIdOrThrow(educationId);
        this.checkOwnershipOrThrow(education, candidateId);

        this.educationRepository.deleteById(educationId);

        log.info("Education deleted: educationId={}, candidateId={}", educationId, candidateId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Education> listEducations(final Long candidateId) {

        log.debug("Listing educations: candidateId={}", candidateId);

        return this.educationRepository.findAllByCandidateId(candidateId);
    }

    private Education findByIdOrThrow(final Long educationId) {
        return this.educationRepository.findById(educationId)
                .orElseThrow(() -> new CandidateResourceNotFoundException(RESOURCE_TYPE, educationId));
    }

    private void checkOwnershipOrThrow(final Education education, final Long candidateId) {
        if (!education.isOwnedBy(candidateId)) {
            throw new CandidateResourceForbiddenException(RESOURCE_TYPE, education.getId(), candidateId);
        }
    }

}