package com.deveyk.jobmatch.company.application.port.out;

import com.deveyk.jobmatch.company.domain.model.Company;

import java.util.Optional;

public interface CompanyRepository {

    Optional<Company> findById(Long id);

    Company save(Company company);

}
