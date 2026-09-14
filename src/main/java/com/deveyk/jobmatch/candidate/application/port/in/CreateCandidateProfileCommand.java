package com.deveyk.jobmatch.candidate.application.port.in;

import com.deveyk.jobmatch.identity.domain.Role;

public record CreateCandidateProfileCommand(
        Long userId,
        Role role
) {

}
