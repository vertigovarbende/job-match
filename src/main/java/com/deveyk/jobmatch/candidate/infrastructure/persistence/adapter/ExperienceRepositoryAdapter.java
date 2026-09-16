package com.deveyk.jobmatch.candidate.infrastructure.persistence.adapter;

import com.deveyk.jobmatch.candidate.application.port.out.ExperienceRepository;
import com.deveyk.jobmatch.candidate.domain.model.Experience;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.entity.ExperienceEntity;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.mapper.ExperiencePersistenceMapper;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.repository.SpringDataExperienceJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExperienceRepositoryAdapter implements ExperienceRepository {

    private final SpringDataExperienceJpaRepository springDataExperienceJpaRepository;
    private final ExperiencePersistenceMapper experiencePersistenceMapper;

    @Override
    public Optional<Experience> findById(final Long id) {
        return this.springDataExperienceJpaRepository.findById(id)
                .map(this.experiencePersistenceMapper::toDomain);
    }

    @Override
    public List<Experience> findAllByCandidateId(final Long candidateId) {
        return this.springDataExperienceJpaRepository.findAllByCandidateId(candidateId).stream()
                .map(this.experiencePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Experience save(final Experience experience) {
        final ExperienceEntity entity = this.experiencePersistenceMapper.toEntity(experience);
        final ExperienceEntity saved = this.springDataExperienceJpaRepository.save(entity);
        return this.experiencePersistenceMapper.toDomain(saved);
    }

    @Override
    public void deleteById(final Long id) {
        this.springDataExperienceJpaRepository.deleteById(id);
    }

}