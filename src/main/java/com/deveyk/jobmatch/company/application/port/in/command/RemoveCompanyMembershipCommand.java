package com.deveyk.jobmatch.company.application.port.in.command;

public record RemoveCompanyMembershipCommand(
        Long companyId,
        Long userId,
        String actorId
) {

}
