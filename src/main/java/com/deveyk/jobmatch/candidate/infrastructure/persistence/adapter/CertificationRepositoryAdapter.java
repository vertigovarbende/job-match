package com.deveyk.jobmatch.candidate.infrastructure.persistence.adapter;

import com.deveyk.jobmatch.candidate.application.port.out.CertificationRepository;
import com.deveyk.jobmatch.candidate.domain.model.Certification;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.entity.CertificationEntity;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.mapper.CertificationPersistenceMapper;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.repository.SpringDataCertificationJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CertificationRepositoryAdapter implements CertificationRepository {

    private final SpringDataCertificationJpaRepository springDataCertificationJpaRepository;
    private final CertificationPersistenceMapper certificationPersistenceMapper;

    @Override
    public Optional<Certification> findById(final Long id) {
        return this.springDataCertificationJpaRepository.findById(id)
                .map(this.certificationPersistenceMapper::toDomain);
    }

    @Override
    public List<Certification> findAllByCandidateId(final Long candidateId) {
        return this.springDataCertificationJpaRepository.findAllByCandidateId(candidateId).stream()
                .map(this.certificationPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Certification save(final Certification certification) {
        final CertificationEntity entity = this.certificationPersistenceMapper.toEntity(certification);
        final CertificationEntity saved = this.springDataCertificationJpaRepository.save(entity);
        return this.certificationPersistenceMapper.toDomain(saved);
    }

    @Override
    public void deleteById(final Long id) {
        this.springDataCertificationJpaRepository.deleteById(id);
    }

}