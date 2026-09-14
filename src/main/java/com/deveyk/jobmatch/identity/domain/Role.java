package com.deveyk.jobmatch.identity.domain;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Role {

    CANDIDATE,
    EMPLOYER,
    ADMIN;

    public static Set<Role> parseKnown(final Collection<String> rawRoleNames) {
        if (rawRoleNames == null) {
            return Set.of();
        }
        return rawRoleNames.stream()
                .flatMap(Role::tryParse)
                .collect(Collectors.toUnmodifiableSet());
    }

    private static Stream<Role> tryParse(final String rawRole) {
        try {
            return Stream.of(Role.valueOf(rawRole));
        } catch (IllegalArgumentException notARecognizedRole) {
            return Stream.empty();
        }
    }

}
