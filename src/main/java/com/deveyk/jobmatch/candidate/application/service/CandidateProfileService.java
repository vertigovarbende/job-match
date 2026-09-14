package com.deveyk.jobmatch.candidate.application.service;

import com.deveyk.jobmatch.candidate.application.port.in.CandidateProfileUseCase;
import com.deveyk.jobmatch.candidate.application.port.in.CreateCandidateProfileCommand;
import com.deveyk.jobmatch.candidate.application.port.in.UpdateCandidateProfileCommand;
import com.deveyk.jobmatch.candidate.application.port.out.CandidateRepository;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateAlreadyExistsException;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateNotFoundException;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateRoleRequiredException;
import com.deveyk.jobmatch.candidate.domain.model.Candidate;
import com.deveyk.jobmatch.identity.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateProfileService implements CandidateProfileUseCase {

    private final CandidateRepository candidateRepository;

    @Override
    @Transactional
    public Candidate createProfile(final CreateCandidateProfileCommand command) {

        log.debug("Creating candidate profile: userId={}, role={}", command.userId(), command.role());

        if (command.role() != Role.CANDIDATE) {
            throw new CandidateRoleRequiredException(command.role());
        }

        if (this.candidateRepository.findByUserId(command.userId()).isPresent()) {
            throw new CandidateAlreadyExistsException(command.userId());
        }

        final Candidate candidate = Candidate.create(command.userId());
        final Candidate saved = this.candidateRepository.save(candidate);

        log.info("Candidate profile created: candidateId={}, userId={}", saved.getId(), saved.getUserId());

        return saved;
    }

    @Override
    @Transactional
    public Candidate updateProfile(final UpdateCandidateProfileCommand command) {

        log.debug("Updating candidate profile: userId={}", command.userId());

        final Candidate candidate = this.findByUserIdOrThrow(command.userId());

        candidate.updateProfile(
                command.headline(),
                command.summary(),
                command.location(),
                command.workplacePreferences(),
                command.desiredSalary()
        );

        final Candidate saved = this.candidateRepository.save(candidate);

        log.info("Candidate profile updated: candidateId={}, userId={}", saved.getId(), saved.getUserId());

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Candidate getProfile(final Long userId) {

        log.debug("Fetching candidate profile: userId={}", userId);

        return this.findByUserIdOrThrow(userId);

    }

    private Candidate findByUserIdOrThrow(final Long userId) {
        return this.candidateRepository.findByUserId(userId)
                .orElseThrow(() -> new CandidateNotFoundException(userId));
    }

}