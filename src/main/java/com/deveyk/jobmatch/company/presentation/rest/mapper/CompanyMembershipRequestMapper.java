package com.deveyk.jobmatch.company.presentation.rest.mapper;

import com.deveyk.jobmatch.company.application.port.in.command.AddCompanyMembershipCommand;
import com.deveyk.jobmatch.company.application.port.in.command.RemoveCompanyMembershipCommand;
import com.deveyk.jobmatch.company.presentation.rest.request.AddCompanyMembershipRequest;
import com.deveyk.jobmatch.identity.domain.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyMembershipRequestMapper {

    AddCompanyMembershipCommand toCommand(AddCompanyMembershipRequest request, Long companyId, Role actorRole, String actorId);

    RemoveCompanyMembershipCommand toCommand(Long companyId, Long userId, String actorId);

}
