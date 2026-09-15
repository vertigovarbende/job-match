// CandidateLanguageRepositoryAdapter.java
package com.deveyk.jobmatch.candidate.infrastructure.persistence.adapter;

import com.deveyk.jobmatch.candidate.application.port.out.CandidateLanguageRepository;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateFieldInvalidException;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateRelationAlreadyExistsException;
import com.deveyk.jobmatch.candidate.domain.model.CandidateLanguage;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.entity.CandidateLanguageEntity;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.mapper.CandidateLanguagePersistenceMapper;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.repository.SpringDataCandidateLanguageJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CandidateLanguageRepositoryAdapter implements CandidateLanguageRepository {

    private static final String UNIQUE_CONSTRAINT_NAME = "uq_jm_candidate_language_candidate_language";
    private static final String LANGUAGE_FK_CONSTRAINT_NAME = "fk_jm_candidate_language_language";

    private final SpringDataCandidateLanguageJpaRepository springDataCandidateLanguageJpaRepository;
    private final CandidateLanguagePersistenceMapper candidateLanguagePersistenceMapper;

    @Override
    public Optional<CandidateLanguage> findByCandidateIdAndLanguageId(final Long candidateId, final Long languageId) {
        return this.springDataCandidateLanguageJpaRepository.findByCandidateIdAndLanguageId(candidateId, languageId)
                .map(this.candidateLanguagePersistenceMapper::toDomain);
    }

    @Override
    public List<CandidateLanguage> findAllByCandidateId(final Long candidateId) {
        return this.springDataCandidateLanguageJpaRepository.findAllByCandidateId(candidateId).stream()
                .map(this.candidateLanguagePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public CandidateLanguage save(final CandidateLanguage candidateLanguage) {
        final CandidateLanguageEntity entity = this.candidateLanguagePersistenceMapper.toEntity(candidateLanguage);
        try {

            final CandidateLanguageEntity saved = this.springDataCandidateLanguageJpaRepository.save(entity);
            return this.candidateLanguagePersistenceMapper.toDomain(saved);

        } catch (DataIntegrityViolationException violation) {

            final String constraintName = extractConstraintName(violation);

            if (UNIQUE_CONSTRAINT_NAME.equals(constraintName)) {

                log.warn("Duplicate jm_candidate_language insert for candidateId={}, languageId={}", candidateLanguage.getCandidateId(), candidateLanguage.getLanguageId());
                throw new CandidateRelationAlreadyExistsException("language", candidateLanguage.getCandidateId(), candidateLanguage.getLanguageId(), violation);

            }

            if (LANGUAGE_FK_CONSTRAINT_NAME.equals(constraintName)) {

                log.warn("Invalid languageId={} referenced for candidateId={}", candidateLanguage.getLanguageId(), candidateLanguage.getCandidateId());
                throw new CandidateFieldInvalidException("languageId", "must reference an existing language");

            }

            throw violation;

        }
    }

    @Override
    public void deleteByCandidateIdAndLanguageId(final Long candidateId, final Long languageId) {
        this.springDataCandidateLanguageJpaRepository.deleteByCandidateIdAndLanguageId(candidateId, languageId);
    }

    private static String extractConstraintName(final DataIntegrityViolationException violation) {
        Throwable cause = violation.getCause();

        while (cause != null) {
            if (cause instanceof ConstraintViolationException constraintViolation) {
                return constraintViolation.getConstraintName();
            }
            cause = cause.getCause();
        }

        return null;
    }

}