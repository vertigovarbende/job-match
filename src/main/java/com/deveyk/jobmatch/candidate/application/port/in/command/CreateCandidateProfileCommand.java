package com.deveyk.jobmatch.candidate.application.port.in.command;

import com.deveyk.jobmatch.identity.domain.Role;

public record CreateCandidateProfileCommand(
        Long userId,
        Role role
) {

}
