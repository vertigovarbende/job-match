package com.deveyk.jobmatch.company.application.port.out;

import com.deveyk.jobmatch.company.domain.model.CompanyMembership;

import java.util.List;
import java.util.Optional;

public interface CompanyMembershipRepository {

    Optional<CompanyMembership> findByCompanyIdAndUserId(Long companyId, Long userId);

    List<CompanyMembership> findAllByCompanyId(Long companyId);

    Optional<CompanyMembership> findByUserId(Long userId);

    CompanyMembership save(CompanyMembership companyMembership);

    void deleteByCompanyIdAndUserId(Long companyId, Long userId);

}
