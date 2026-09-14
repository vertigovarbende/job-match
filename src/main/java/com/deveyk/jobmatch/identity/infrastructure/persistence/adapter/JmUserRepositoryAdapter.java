package com.deveyk.jobmatch.identity.infrastructure.persistence.adapter;

import com.deveyk.jobmatch.identity.domain.exception.JmUserAlreadyExistsException;
import com.deveyk.jobmatch.identity.domain.model.JmUser;
import com.deveyk.jobmatch.identity.domain.repository.JmUserRepository;
import com.deveyk.jobmatch.identity.infrastructure.persistence.entity.JmUserEntity;
import com.deveyk.jobmatch.identity.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.deveyk.jobmatch.identity.infrastructure.persistence.repository.SpringDataJmUserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JmUserRepositoryAdapter implements JmUserRepository {

    private final SpringDataJmUserJpaRepository springDataJmUserJpaRepository;
    private final UserPersistenceMapper jmUserEntityMapper;

    @Override
    public Optional<JmUser> findByKeycloakSubjectId(UUID keycloakSubjectId) {
        return this.springDataJmUserJpaRepository.findByKeycloakSubjectId(keycloakSubjectId)
                .map(this.jmUserEntityMapper::toDomain);
    }

    @Override
    public JmUser save(JmUser jmUser) {
        final JmUserEntity entity = this.jmUserEntityMapper.toEntity(jmUser);
        try {

            final JmUserEntity saved = this.springDataJmUserJpaRepository.save(entity);
            return this.jmUserEntityMapper.toDomain(saved);

        } catch (DataIntegrityViolationException concurrentInsert) {

            log.warn("Concurrent jm_user insert race lost for keycloakSubjectId={}", jmUser.getKeycloakSubjectId());
            throw new JmUserAlreadyExistsException(jmUser.getKeycloakSubjectId(), concurrentInsert);

        }
    }

}
