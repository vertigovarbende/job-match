package com.deveyk.jobmatch.company.presentation.rest;

import com.deveyk.jobmatch.company.application.port.in.CompanyProfileUseCase;
import com.deveyk.jobmatch.company.application.port.in.command.VerifyCompanyCommand;
import com.deveyk.jobmatch.company.domain.model.Company;
import com.deveyk.jobmatch.company.presentation.rest.mapper.CompanyProfileRequestMapper;
import com.deveyk.jobmatch.company.presentation.rest.mapper.CompanyProfileResponseMapper;
import com.deveyk.jobmatch.company.presentation.rest.request.CreateCompanyRequest;
import com.deveyk.jobmatch.company.presentation.rest.request.UpdateCompanyProfileRequest;
import com.deveyk.jobmatch.company.presentation.rest.response.CompanyResponse;
import com.deveyk.jobmatch.identity.application.CurrentUserFacade;
import com.deveyk.jobmatch.identity.domain.model.JmUser;
import com.deveyk.jobmatch.shared.presentation.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CompanyProfileController {

    private final CompanyProfileUseCase companyProfileUseCase;
    private final CompanyProfileRequestMapper companyProfileRequestMapper;
    private final CompanyProfileResponseMapper companyProfileResponseMapper;
    private final CurrentUserFacade currentUserFacade;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(CompanyApiPaths.Profile.BASE)
    public BaseResponse<CompanyResponse> createProfile(@Valid @RequestBody final CreateCompanyRequest request) {

        final JmUser currentUser = this.currentUserFacade.resolveCurrentUser();
        final Company company = this.companyProfileUseCase.createProfile(this.companyProfileRequestMapper.toCommand(request, currentUser.getRole()));

        return BaseResponse.success(this.companyProfileResponseMapper.toResponse(company));
    }

    @GetMapping(CompanyApiPaths.Profile.BY_ID)
    public BaseResponse<CompanyResponse> getProfile(@PathVariable final Long companyId) {

        final Company company = this.companyProfileUseCase.getProfile(companyId);

        return BaseResponse.success(this.companyProfileResponseMapper.toResponse(company));
    }

    @PutMapping(CompanyApiPaths.Profile.BY_ID)
    public BaseResponse<CompanyResponse> updateProfile(@PathVariable final Long companyId, @Valid @RequestBody final UpdateCompanyProfileRequest request) {

        final Company company = this.companyProfileUseCase.updateProfile(this.companyProfileRequestMapper.toCommand(request, companyId));

        return BaseResponse.success(this.companyProfileResponseMapper.toResponse(company));
    }

    @PostMapping(CompanyApiPaths.Profile.VERIFY)
    public BaseResponse<CompanyResponse> verify(@PathVariable final Long companyId) {

        final JmUser currentUser = this.currentUserFacade.resolveCurrentUser();
        final VerifyCompanyCommand command = this.companyProfileRequestMapper.toCommand(companyId, String.valueOf(currentUser.getId()));
        final Company company = this.companyProfileUseCase.verify(command);

        return BaseResponse.success(this.companyProfileResponseMapper.toResponse(company));
    }

}
