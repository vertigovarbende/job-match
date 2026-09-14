package com.deveyk.jobmatch.identity.application;

import com.deveyk.jobmatch.identity.domain.exception.JmUserAlreadyExistsException;
import com.deveyk.jobmatch.identity.domain.model.JmUser;
import com.deveyk.jobmatch.identity.domain.repository.JmUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserResolver {

    private final JmUserRepository jmUserRepository;

    public JmUser resolve(final AuthenticatedUserClaims claims) {
        return this.jmUserRepository.findByKeycloakSubjectId(claims.subjectId())
                .map(existing -> syncIfNeeded(existing, claims))
                .orElseGet(() -> createOrFetchIfRaceLost(claims));
    }

    private JmUser syncIfNeeded(final JmUser existing, final AuthenticatedUserClaims claims) {
        if (existing.isSame(claims.email(), claims.role())) {
            return existing;
        }
        existing.synchronize(claims.email(), claims.role());
        return this.jmUserRepository.save(existing);
    }

    private JmUser createOrFetchIfRaceLost(final AuthenticatedUserClaims claims) {

        try {

            final JmUser dbUser = JmUser.register(claims.subjectId(), claims.email(), claims.role());
            return this.jmUserRepository.save(dbUser);

        } catch (JmUserAlreadyExistsException concurrentInsert) {

            return this.jmUserRepository.findByKeycloakSubjectId(claims.subjectId())
                    .orElseThrow(() -> concurrentInsert);

        }
    }

}
