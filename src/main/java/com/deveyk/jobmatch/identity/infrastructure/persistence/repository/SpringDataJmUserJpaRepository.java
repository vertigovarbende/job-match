package com.deveyk.jobmatch.identity.infrastructure.persistence.repository;

import com.deveyk.jobmatch.identity.infrastructure.persistence.entity.JmUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJmUserJpaRepository extends JpaRepository<JmUserEntity, Long> {

    Optional<JmUserEntity> findByKeycloakSubjectId(UUID keycloakSubjectId);

}
