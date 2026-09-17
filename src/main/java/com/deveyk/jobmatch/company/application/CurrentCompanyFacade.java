package com.deveyk.jobmatch.company.application;

import com.deveyk.jobmatch.company.application.port.in.CompanyMembershipUseCase;
import com.deveyk.jobmatch.company.application.port.in.CompanyProfileUseCase;
import com.deveyk.jobmatch.company.domain.model.Company;
import com.deveyk.jobmatch.company.domain.model.CompanyMembership;
import com.deveyk.jobmatch.identity.application.CurrentUserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrentCompanyFacade {

    private final CurrentUserFacade currentUserFacade;
    private final CompanyMembershipUseCase companyMembershipUseCase;
    private final CompanyProfileUseCase companyProfileUseCase;

    public Optional<Company> resolveCurrentCompany() {

        final Long userId = this.currentUserFacade.resolveCurrentUser().getId();

        return this.companyMembershipUseCase.findMembershipByUserId(userId)
                .map(CompanyMembership::getCompanyId)
                .map(this.companyProfileUseCase::getProfile);

    }

    public Optional<Long> resolveCurrentCompanyId() {

        final Long userId = this.currentUserFacade.resolveCurrentUser().getId();

        return this.companyMembershipUseCase.findMembershipByUserId(userId)
                .map(CompanyMembership::getCompanyId);

    }

}
