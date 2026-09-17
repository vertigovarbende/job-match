package com.deveyk.jobmatch.candidate.application.service;

import com.deveyk.jobmatch.candidate.application.port.in.command.AttachCandidateLanguageCommand;
import com.deveyk.jobmatch.candidate.application.port.in.CandidateLanguageUseCase;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateCandidateLanguageCommand;
import com.deveyk.jobmatch.candidate.application.port.out.CandidateLanguageRepository;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateResourceNotFoundException;
import com.deveyk.jobmatch.candidate.domain.model.CandidateLanguage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateLanguageService implements CandidateLanguageUseCase {

    private static final String RESOURCE_TYPE = "CandidateLanguage";

    private final CandidateLanguageRepository candidateLanguageRepository;

    @Override
    @Transactional
    public CandidateLanguage attachLanguage(final AttachCandidateLanguageCommand command) {

        log.debug("Attaching language: candidateId={}, languageId={}", command.candidateId(), command.languageId());

        final CandidateLanguage candidateLanguage = CandidateLanguage.create(
                command.candidateId(),
                command.languageId(),
                command.proficiencyLevel()
        );

        final CandidateLanguage saved = this.candidateLanguageRepository.save(candidateLanguage);

        log.info("Language attached: candidateId={}, languageId={}", saved.getCandidateId(), saved.getLanguageId());

        return saved;
    }

    @Override
    @Transactional
    public CandidateLanguage updateLanguageProficiency(final UpdateCandidateLanguageCommand command) {

        log.debug("Updating language proficiency: candidateId={}, languageId={}", command.candidateId(), command.languageId());

        final CandidateLanguage candidateLanguage = this.findOrThrow(command.candidateId(), command.languageId());
        candidateLanguage.updateProficiency(command.proficiencyLevel());

        final CandidateLanguage saved = this.candidateLanguageRepository.save(candidateLanguage);

        log.info("Language proficiency updated: candidateId={}, languageId={}", saved.getCandidateId(), saved.getLanguageId());

        return saved;
    }

    @Override
    @Transactional
    public void detachLanguage(final Long candidateId, final Long languageId) {

        log.debug("Detaching language: candidateId={}, languageId={}", candidateId, languageId);

        this.findOrThrow(candidateId, languageId);
        this.candidateLanguageRepository.deleteByCandidateIdAndLanguageId(candidateId, languageId);

        log.info("Language detached: candidateId={}, languageId={}", candidateId, languageId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CandidateLanguage> listLanguages(final Long candidateId) {

        log.debug("Listing languages: candidateId={}", candidateId);

        return this.candidateLanguageRepository.findAllByCandidateId(candidateId);
    }

    private CandidateLanguage findOrThrow(final Long candidateId, final Long languageId) {
        return this.candidateLanguageRepository.findByCandidateIdAndLanguageId(candidateId, languageId)
                .orElseThrow(() -> new CandidateResourceNotFoundException(RESOURCE_TYPE, languageId));
    }

}