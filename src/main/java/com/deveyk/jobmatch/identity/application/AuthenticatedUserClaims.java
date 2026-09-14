package com.deveyk.jobmatch.identity.application;

import com.deveyk.jobmatch.identity.domain.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Builder
public record AuthenticatedUserClaims(
        UUID subjectId,
        String email,
        Role role
) {

}
