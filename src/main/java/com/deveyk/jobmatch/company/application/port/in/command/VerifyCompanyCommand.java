package com.deveyk.jobmatch.company.application.port.in.command;

public record VerifyCompanyCommand(
        Long id,
        String actorId
) {

}
