package com.deveyk.jobmatch.company.application.port.in;

import com.deveyk.jobmatch.company.application.port.in.command.AddCompanyMembershipCommand;
import com.deveyk.jobmatch.company.application.port.in.command.RemoveCompanyMembershipCommand;
import com.deveyk.jobmatch.company.domain.model.CompanyMembership;

import java.util.List;
import java.util.Optional;

public interface CompanyMembershipUseCase {

    CompanyMembership addMember(AddCompanyMembershipCommand command);

    void removeMember(RemoveCompanyMembershipCommand command);

    List<CompanyMembership> listMembers(Long companyId);

    Optional<CompanyMembership> findMembershipByUserId(Long userId);

}
