package com.deveyk.jobmatch.candidate.infrastructure.persistence.adapter;

import com.deveyk.jobmatch.candidate.application.port.out.CandidateSkillRepository;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateFieldInvalidException;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateRelationAlreadyExistsException;
import com.deveyk.jobmatch.candidate.domain.model.CandidateSkill;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.entity.CandidateSkillEntity;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.mapper.CandidateSkillPersistenceMapper;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.repository.SpringDataCandidateSkillJpaRepository;
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
public class CandidateSkillRepositoryAdapter implements CandidateSkillRepository {

    private static final String UNIQUE_CONSTRAINT_NAME = "uq_jm_candidate_skill_candidate_skill";
    private static final String SKILL_FK_CONSTRAINT_NAME = "fk_jm_candidate_skill_skill";

    private final SpringDataCandidateSkillJpaRepository springDataCandidateSkillJpaRepository;
    private final CandidateSkillPersistenceMapper candidateSkillPersistenceMapper;

    @Override
    public Optional<CandidateSkill> findByCandidateIdAndSkillId(final Long candidateId, final Long skillId) {
        return this.springDataCandidateSkillJpaRepository.findByCandidateIdAndSkillId(candidateId, skillId)
                .map(this.candidateSkillPersistenceMapper::toDomain);
    }

    @Override
    public List<CandidateSkill> findAllByCandidateId(final Long candidateId) {
        return this.springDataCandidateSkillJpaRepository.findAllByCandidateId(candidateId).stream()
                .map(this.candidateSkillPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public CandidateSkill save(final CandidateSkill candidateSkill) {
        final CandidateSkillEntity entity = this.candidateSkillPersistenceMapper.toEntity(candidateSkill);
        try {

            final CandidateSkillEntity saved = this.springDataCandidateSkillJpaRepository.save(entity);
            return this.candidateSkillPersistenceMapper.toDomain(saved);

        } catch (DataIntegrityViolationException violation) {

            final String constraintName = extractConstraintName(violation);

            if (UNIQUE_CONSTRAINT_NAME.equals(constraintName)) {

                log.warn("Duplicate jm_candidate_skill insert for candidateId={}, skillId={}", candidateSkill.getCandidateId(), candidateSkill.getSkillId());
                throw new CandidateRelationAlreadyExistsException("skill", candidateSkill.getCandidateId(), candidateSkill.getSkillId(), violation);

            }

            if (SKILL_FK_CONSTRAINT_NAME.equals(constraintName)) {

                log.warn("Invalid skillId={} referenced for candidateId={}", candidateSkill.getSkillId(), candidateSkill.getCandidateId());
                throw new CandidateFieldInvalidException("skillId", "must reference an existing skill");

            }

            throw violation;

        }
    }

    @Override
    public void deleteByCandidateIdAndSkillId(final Long candidateId, final Long skillId) {
        this.springDataCandidateSkillJpaRepository.deleteByCandidateIdAndSkillId(candidateId, skillId);
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