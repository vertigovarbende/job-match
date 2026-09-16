package com.deveyk.jobmatch.candidate.infrastructure.persistence.adapter;

import com.deveyk.jobmatch.candidate.application.port.out.EducationRepository;
import com.deveyk.jobmatch.candidate.domain.model.Education;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.entity.EducationEntity;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.mapper.EducationPersistenceMapper;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.repository.SpringDataEducationJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EducationRepositoryAdapter implements EducationRepository {

    private final SpringDataEducationJpaRepository springDataEducationJpaRepository;
    private final EducationPersistenceMapper educationPersistenceMapper;

    @Override
    public Optional<Education> findById(final Long id) {
        return this.springDataEducationJpaRepository.findById(id)
                .map(this.educationPersistenceMapper::toDomain);
    }

    @Override
    public List<Education> findAllByCandidateId(final Long candidateId) {
        return this.springDataEducationJpaRepository.findAllByCandidateId(candidateId).stream()
                .map(this.educationPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Education save(final Education education) {
        final EducationEntity entity = this.educationPersistenceMapper.toEntity(education);
        final EducationEntity saved = this.springDataEducationJpaRepository.save(entity);
        return this.educationPersistenceMapper.toDomain(saved);
    }

    @Override
    public void deleteById(final Long id) {
        this.springDataEducationJpaRepository.deleteById(id);
    }

}