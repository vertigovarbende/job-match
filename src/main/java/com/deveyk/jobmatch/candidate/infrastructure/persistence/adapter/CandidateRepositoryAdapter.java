package com.deveyk.jobmatch.candidate.infrastructure.persistence.adapter;

import com.deveyk.jobmatch.candidate.domain.exception.CandidateAlreadyExistsException;
import com.deveyk.jobmatch.candidate.domain.model.Candidate;
import com.deveyk.jobmatch.candidate.application.port.out.CandidateRepository;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.entity.CandidateEntity;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.mapper.CandidatePersistenceMapper;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.repository.SpringDataCandidateJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CandidateRepositoryAdapter implements CandidateRepository {

    private final SpringDataCandidateJpaRepository springDataCandidateJpaRepository;
    private final CandidatePersistenceMapper candidatePersistenceMapper;

    @Override
    public Optional<Candidate> findByUserId(final Long userId) {
        return this.springDataCandidateJpaRepository.findByUserId(userId)
                .map(this.candidatePersistenceMapper::toDomain);
    }

    @Override
    public Candidate save(final Candidate candidate) {
        final CandidateEntity entity = this.candidatePersistenceMapper.toEntity(candidate);
        try {

            final CandidateEntity saved = this.springDataCandidateJpaRepository.save(entity);
            return this.candidatePersistenceMapper.toDomain(saved);

        } catch (DataIntegrityViolationException concurrentInsert) {

            log.warn("Concurrent jm_candidate insert race lost for userId={}", candidate.getUserId());
            throw new CandidateAlreadyExistsException(candidate.getUserId(), concurrentInsert);

        }
    }

}