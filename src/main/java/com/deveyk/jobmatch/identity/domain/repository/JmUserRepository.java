package com.deveyk.jobmatch.identity.domain.repository;

import com.deveyk.jobmatch.identity.domain.model.JmUser;

import java.util.Optional;
import java.util.UUID;

public interface JmUserRepository {

    Optional<JmUser> findByKeycloakSubjectId(UUID keycloakSubjectId);

    JmUser save(JmUser jmUser);

}
