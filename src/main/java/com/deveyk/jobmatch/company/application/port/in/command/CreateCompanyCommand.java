package com.deveyk.jobmatch.company.application.port.in.command;

import com.deveyk.jobmatch.identity.domain.Role;

public record CreateCompanyCommand(
        String name,
        Role role
) {

}
