package com.deveyk.jobmatch.candidate.application.service;

import com.deveyk.jobmatch.candidate.application.port.in.command.AddExperienceCommand;
import com.deveyk.jobmatch.candidate.application.port.in.ExperienceUseCase;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateExperienceCommand;
import com.deveyk.jobmatch.candidate.application.port.out.ExperienceRepository;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateResourceForbiddenException;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateResourceNotFoundException;
import com.deveyk.jobmatch.candidate.domain.model.Experience;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExperienceService implements ExperienceUseCase {

    private static final String RESOURCE_TYPE = "Experience";

    private final ExperienceRepository experienceRepository;

    @Override
    @Transactional
    public Experience addExperience(final AddExperienceCommand command) {

        log.debug("Adding experience: candidateId={}, title={}", command.candidateId(), command.title());

        final Experience experience = Experience.create(
                command.candidateId(),
                command.title(),
                command.company(),
                command.location(),
                command.employmentType(),
                command.startDate(),
                command.endDate(),
                command.description()
        );

        final Experience saved = this.experienceRepository.save(experience);

        log.info("Experience added: experienceId={}, candidateId={}", saved.getId(), saved.getCandidateId());

        return saved;
    }

    @Override
    @Transactional
    public Experience updateExperience(final UpdateExperienceCommand command) {

        log.debug("Updating experience: candidateId={}, experienceId={}", command.candidateId(), command.experienceId());

        final Experience experience = this.findByIdOrThrow(command.experienceId());
        this.checkOwnershipOrThrow(experience, command.candidateId());

        experience.update(
                command.title(),
                command.company(),
                command.location(),
                command.employmentType(),
                command.startDate(),
                command.endDate(),
                command.description()
        );

        final Experience saved = this.experienceRepository.save(experience);

        log.info("Experience updated: experienceId={}, candidateId={}", saved.getId(), saved.getCandidateId());

        return saved;
    }

    @Override
    @Transactional
    public void deleteExperience(final Long candidateId, final Long experienceId) {

        log.debug("Deleting experience: candidateId={}, experienceId={}", candidateId, experienceId);

        final Experience experience = this.findByIdOrThrow(experienceId);
        this.checkOwnershipOrThrow(experience, candidateId);

        this.experienceRepository.deleteById(experienceId);

        log.info("Experience deleted: experienceId={}, candidateId={}", experienceId, candidateId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Experience> listExperiences(final Long candidateId) {

        log.debug("Listing experiences: candidateId={}", candidateId);

        return this.experienceRepository.findAllByCandidateId(candidateId);
    }

    private Experience findByIdOrThrow(final Long experienceId) {
        return this.experienceRepository.findById(experienceId)
                .orElseThrow(() -> new CandidateResourceNotFoundException(RESOURCE_TYPE, experienceId));
    }

    private void checkOwnershipOrThrow(final Experience experience, final Long candidateId) {
        if (!experience.isOwnedBy(candidateId)) {
            throw new CandidateResourceForbiddenException(RESOURCE_TYPE, experience.getId(), candidateId);
        }
    }

}