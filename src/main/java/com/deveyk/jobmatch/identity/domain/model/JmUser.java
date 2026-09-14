package com.deveyk.jobmatch.identity.domain.model;

import com.deveyk.jobmatch.identity.domain.Role;
import com.deveyk.jobmatch.shared.domain.Guard;
import com.deveyk.jobmatch.shared.domain.model.JmBaseDomain;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public final class JmUser extends JmBaseDomain {

    private final Long id;
    private final UUID keycloakSubjectId;
    private String firstName;
    private String lastName;
    private String email;
    private LoginAttempt loginAttempt;
    private Role role;

    @Getter
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    public static class LoginAttempt extends JmBaseDomain {

        private Long id;
        private LocalDateTime lastLogin;

    }

    public static JmUser register(final UUID keycloakSubjectId, final String email, final Role role) {
        return JmUser.builder()
                .id(null)
                .keycloakSubjectId(keycloakSubjectId)
                .email(email)
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void synchronize(final String email, final Role role) {
        this.email = Guard.requireNonBlank(email, "email");
        this.role = Objects.requireNonNull(role, "role must not be null");
    }

    public boolean isSame(final String email, final Role role) {
        return this.email.equals(email) && this.role == role;
    }

}
