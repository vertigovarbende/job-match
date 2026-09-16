package com.deveyk.jobmatch.company.application.port.in.command;

import com.deveyk.jobmatch.identity.domain.Role;

public record AddCompanyMembershipCommand(
        Long companyId,
        Long userId,
        Role actorRole,
        String actorId
) {

}
