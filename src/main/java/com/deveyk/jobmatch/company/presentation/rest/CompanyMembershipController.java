package com.deveyk.jobmatch.company.presentation.rest;

import com.deveyk.jobmatch.company.application.port.in.CompanyMembershipUseCase;
import com.deveyk.jobmatch.company.application.port.in.command.AddCompanyMembershipCommand;
import com.deveyk.jobmatch.company.application.port.in.command.RemoveCompanyMembershipCommand;
import com.deveyk.jobmatch.company.domain.model.CompanyMembership;
import com.deveyk.jobmatch.company.presentation.rest.mapper.CompanyMembershipRequestMapper;
import com.deveyk.jobmatch.company.presentation.rest.mapper.CompanyMembershipResponseMapper;
import com.deveyk.jobmatch.company.presentation.rest.request.AddCompanyMembershipRequest;
import com.deveyk.jobmatch.company.presentation.rest.response.CompanyMembershipResponse;
import com.deveyk.jobmatch.identity.application.CurrentUserFacade;
import com.deveyk.jobmatch.identity.domain.model.JmUser;
import com.deveyk.jobmatch.shared.presentation.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CompanyMembershipController {

    private final CompanyMembershipUseCase companyMembershipUseCase;
    private final CompanyMembershipRequestMapper companyMembershipRequestMapper;
    private final CompanyMembershipResponseMapper companyMembershipResponseMapper;
    private final CurrentUserFacade currentUserFacade;

    @PostMapping(CompanyApiPaths.Membership.BASE)
    public BaseResponse<CompanyMembershipResponse> addMember(@PathVariable final Long companyId, @Valid @RequestBody final AddCompanyMembershipRequest request) {

        final JmUser currentUser = this.currentUserFacade.resolveCurrentUser();
        final AddCompanyMembershipCommand command = this.companyMembershipRequestMapper.toCommand(request, companyId, currentUser.getRole(), String.valueOf(currentUser.getId()));
        final CompanyMembership companyMembership = this.companyMembershipUseCase.addMember(command);

        return BaseResponse.success(this.companyMembershipResponseMapper.toResponse(companyMembership));
    }

    @DeleteMapping(CompanyApiPaths.Membership.BY_USER_ID)
    public BaseResponse<Void> removeMember(@PathVariable final Long companyId, @PathVariable final Long userId) {

        final JmUser currentUser = this.currentUserFacade.resolveCurrentUser();
        final RemoveCompanyMembershipCommand command = this.companyMembershipRequestMapper.toCommand(companyId, userId, String.valueOf(currentUser.getId()));
        this.companyMembershipUseCase.removeMember(command);

        return BaseResponse.success();
    }

    @GetMapping(CompanyApiPaths.Membership.BASE)
    public BaseResponse<List<CompanyMembershipResponse>> listMembers(@PathVariable final Long companyId) {

        final List<CompanyMembership> companyMemberships = this.companyMembershipUseCase.listMembers(companyId);

        return BaseResponse.success(this.companyMembershipResponseMapper.toResponseList(companyMemberships));
    }

}
