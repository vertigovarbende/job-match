package com.deveyk.jobmatch.company.application.port.in;

import com.deveyk.jobmatch.company.application.port.in.command.CreateCompanyCommand;
import com.deveyk.jobmatch.company.application.port.in.command.UpdateCompanyProfileCommand;
import com.deveyk.jobmatch.company.application.port.in.command.VerifyCompanyCommand;
import com.deveyk.jobmatch.company.domain.model.Company;

public interface CompanyProfileUseCase {

    Company createProfile(CreateCompanyCommand command);

    Company updateProfile(UpdateCompanyProfileCommand command);

    Company getProfile(Long id);

    Company verify(VerifyCompanyCommand command);

}
